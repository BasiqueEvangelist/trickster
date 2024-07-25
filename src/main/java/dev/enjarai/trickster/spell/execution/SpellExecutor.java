package dev.enjarai.trickster.spell.execution;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class SpellExecutor {
    protected final Stack<SpellInstruction> instructions = new Stack<>();
    protected final Stack<Fragment> inputs = new Stack<>();
    protected final Stack<Integer> scope = new Stack<>();
    protected ExecutionState state;
    protected Optional<SpellExecutor> child = Optional.empty();
    protected Optional<Fragment> overrideReturnValue = Optional.empty();

    protected int lastRunExecutions;

    public static final Codec<SpellExecutor> CODEC = Codec.recursive("spell_executor", self -> RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(SerializedSpellInstruction.CODEC).fieldOf("instructions").forGetter(spellQueue -> spellQueue.instructions.stream().map(SpellInstruction::asSerialized).collect(Collectors.toList())),
            Codec.list(Fragment.CODEC.get().codec()).fieldOf("inputs").forGetter(spellQueue -> spellQueue.inputs),
            Codec.list(Codec.INT).fieldOf("scope").forGetter(spellQueue -> spellQueue.scope),
            ExecutionState.CODEC.fieldOf("state").forGetter(spellQueue -> spellQueue.state),
            self.optionalFieldOf("child").forGetter(spellQueue -> spellQueue.child),
            Fragment.CODEC.get().codec().optionalFieldOf("override_return_value").forGetter(spellQueue -> spellQueue.overrideReturnValue)
    ).apply(instance, (instructions, inputs, scope, state, child, overrideReturnValue) -> {
        List<SpellInstruction> serializedInstructions = instructions.stream().map(SerializedSpellInstruction::toDeserialized).collect(Collectors.toList());
        return new SpellExecutor(serializedInstructions, inputs, scope, state, child, overrideReturnValue);
    })));

    private SpellExecutor(List<SpellInstruction> instructions, List<Fragment> inputs, List<Integer> scope, ExecutionState state, Optional<SpellExecutor> child, Optional<Fragment> overrideReturnValue) {
        this.instructions.addAll(instructions);
        this.inputs.addAll(inputs);
        this.scope.addAll(scope);
        this.state = state;
        this.child = child;
        this.overrideReturnValue = overrideReturnValue;
    }

    public SpellExecutor(SpellPart root, List<Fragment> arguments) {
        this.state = new ExecutionState(arguments);
        flattenNode(root);
    }

    public SpellExecutor(SpellPart root, ExecutionState executionState) {
        this.state = executionState;
        flattenNode(root);
    }

    protected void flattenNode(SpellPart node) {
        instructions.push(new ExitScopeInstruction());
        instructions.push(node.glyph);

        for (var subNode : node.subParts.reversed()) {
            flattenNode(subNode);
        }

        instructions.push(new EnterScopeInstruction());
    }

    /**
     * @return the spell's result, or Optional.empty() if the spell is not done executing.
     * @throws BlunderException
     */
    public Optional<Fragment> run(SpellSource source) throws BlunderException {
        return run(new SpellContext(source, state), 0);
    }

    /**
     * @return the spell's result, or Optional.empty() if the spell is not done executing.
     * @throws BlunderException
     */
    protected Optional<Fragment> run(SpellContext ctx, int executions) throws BlunderException {
        lastRunExecutions = 0;

        if (child.isPresent())
        {
            var result = runChild(ctx, executions);

            if (result.isEmpty())
                return result;
        }

        while (true) {
            if (executions >= Trickster.CONFIG.maxExecutionsPerSpellPerTick()) {
                return Optional.empty();
            }

            var inst = instructions.pop();

            if (inst instanceof EnterScopeInstruction) {
                scope.push(0);
            } else if (inst instanceof ExitScopeInstruction) {
                scope.pop();

                if (scope.isEmpty())
                    return overrideReturnValue.or(() -> Optional.of(inputs.pop()));

                scope.push(scope.pop() + 1);
            } else {
                List<Fragment> args;
                {
                    var _args = new ArrayList<Fragment>();
                    for (int i = scope.peek(); i > 0; i--)
                        _args.add(inputs.pop());
                    args = _args.reversed();
                }

                if (inst.forks(ctx, args)) {
                    var child = makeExecutor(ctx, inst, args);
                    var isTail = true;
                    Fragment returnValue = null;

                    // This may be a bit jank, but it should™ usually fail fast and not cause issues
                    if (instructions.size() > 1) {
                        for (var instruction : instructions) {
                            if (instruction instanceof ExitScopeInstruction) {
                                continue;
                            } else if (instruction instanceof PatternGlyph patternGlyph && patternGlyph.pattern().isEmpty()) {
                                returnValue = VoidFragment.INSTANCE;
                                continue;
                            }
                            isTail = false;
                            break;
                        }
                    }

                    if (isTail) {
                        instructions.clear();
                        inputs.clear();
                        scope.clear();

                        // We need to be able to do this to deal with subcircles return values being gobbled up.
                        // Hopefully in the future, we can also adjust this to work for literals.
                        if (overrideReturnValue.isEmpty()) {
                            overrideReturnValue = Optional.ofNullable(returnValue);
                        }

                        instructions.addAll(child.instructions);
                        state = child.state;
                        // The new state will already have incremented recursion count, but since this isn't
                        // *technically* a recursion, we can just decrement it again.
                        state.decrementRecursions();
                        ctx = new SpellContext(ctx.source(), state);
                    } else {
                        this.child = Optional.of(child);
                        var result = runChild(ctx, executions);

                        if (result.isEmpty()) {
                            return result;
                        }
                    }
                } else {
                    inputs.push(inst.getActivator().orElseThrow(UnsupportedOperationException::new).apply(ctx, args));
                }

                executions++;
                lastRunExecutions = executions;
            }
        }
    }

    protected Optional<Fragment> runChild(SpellContext ctx, int executions) {
        var result = child.flatMap(c -> c.run(new SpellContext(ctx.source(), c.state), executions));

        if (result.isPresent()) {
            inputs.push(result.get());
            child = Optional.empty();
        }

        return result;
    }

    protected static SpellExecutor makeExecutor(SpellContext context, SpellInstruction inst, List<Fragment> args) throws BlunderException {
        return inst.makeFork(context, args);
    }

    public int getLastRunExecutions() {
        return child.map(SpellExecutor::getLastRunExecutions).orElse(lastRunExecutions);
    }
}
