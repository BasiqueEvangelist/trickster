package dev.enjarai.trickster.spell.tricks;

import com.mojang.serialization.Lifecycle;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.tricks.func.ClosureTrick;
import dev.enjarai.trickster.spell.tricks.func.ExecuteTrick;
import dev.enjarai.trickster.spell.tricks.func.LoadArgumentTrick;
import dev.enjarai.trickster.spell.tricks.basic.*;
import dev.enjarai.trickster.spell.tricks.block.*;
import dev.enjarai.trickster.spell.tricks.bool.*;
import dev.enjarai.trickster.spell.tricks.entity.*;
import dev.enjarai.trickster.spell.tricks.event.CreateSpellCircleTrick;
import dev.enjarai.trickster.spell.tricks.event.DeleteSpellCircleTrick;
import dev.enjarai.trickster.spell.tricks.list.*;
import dev.enjarai.trickster.spell.tricks.math.*;
import dev.enjarai.trickster.spell.tricks.vector.*;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Tricks {
    private static final Map<Pattern, Trick> LOOKUP = new HashMap<>();

    public static final RegistryKey<Registry<Trick>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("trick"));
    public static final Registry<Trick> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable()) {
        @Override
        public RegistryEntry.Reference<Trick> add(RegistryKey<Trick> key, Trick value, RegistryEntryInfo info) {
            LOOKUP.put(value.getPattern(), value);
            return super.add(key, value, info);
        }
    };

    // Functions
    public static final ExecuteTrick EXECUTE = register("execute", new ExecuteTrick());
    public static final ClosureTrick CLOSURE = register("closure", new ClosureTrick());
    public static final LoadArgumentTrick LOAD_ARGUMENT_1 = register("load_argument_1", new LoadArgumentTrick(Pattern.of(4, 1), 0));
    public static final LoadArgumentTrick LOAD_ARGUMENT_2 = register("load_argument_2", new LoadArgumentTrick(Pattern.of(4, 2), 1));
    public static final LoadArgumentTrick LOAD_ARGUMENT_3 = register("load_argument_3", new LoadArgumentTrick(Pattern.of(4, 5), 2));
    public static final LoadArgumentTrick LOAD_ARGUMENT_4 = register("load_argument_4", new LoadArgumentTrick(Pattern.of(4, 8), 3));
    public static final LoadArgumentTrick LOAD_ARGUMENT_5 = register("load_argument_5", new LoadArgumentTrick(Pattern.of(4, 7), 4));
    public static final LoadArgumentTrick LOAD_ARGUMENT_6 = register("load_argument_6", new LoadArgumentTrick(Pattern.of(4, 6), 5));
    public static final LoadArgumentTrick LOAD_ARGUMENT_7 = register("load_argument_7", new LoadArgumentTrick(Pattern.of(4, 3), 6));
    public static final LoadArgumentTrick LOAD_ARGUMENT_8 = register("load_argument_8", new LoadArgumentTrick(Pattern.of(4, 0), 7));

    // Basic
    public static final OnePonyTrick TWO = register("two", new OnePonyTrick());
    public static final RevealTrick REVEAL = register("reveal", new RevealTrick());
    public static final ReadSpellTrick READ_SPELL = register("read_spell", new ReadSpellTrick());
    public static final WriteSpellTrick WRITE_SPELL = register("write_spell", new WriteSpellTrick());
    public static final ReadCrowMindTrick READ_CROW_MIND = register("read_crow_mind", new ReadCrowMindTrick());
    public static final WriteCrowMindTrick WRITE_CROW_MIND = register("write_crow_mind", new WriteCrowMindTrick());

    // Caster
    public static final ReflectionTrick REFLECTION = register("reflection", new ReflectionTrick());
    public static final CasterReflection CASTER_REFLECTION = register("caster_reflection", new CasterReflection());
//    public static final CostTrick COST = register("cost", new CostTrick());

    // Entity
    public static final GetPositionTrick GET_POSITION = register("get_position", new GetPositionTrick());
    public static final GetFacingTrick GET_FACING = register("get_facing", new GetFacingTrick());
    public static final HeightReflectionTrick HEIGHT_REFLECTION = register("height_reflection", new HeightReflectionTrick());
    public static final SneakingReflectionTrick SNEAKING_REFLECTION = register("sneaking_reflection", new SneakingReflectionTrick());
    public static final RaycastBlockPosTrick RAYCAST = register("raycast", new RaycastBlockPosTrick());
//    public static final RaycastBlockSideTrick RAYCAST_SIDE = register("raycast_side", new RaycastBlockSideTrick());
//    public static final RaycastEntityTrick RAYCAST_ENTITY = register("raycast_entity", new RaycastEntityTrick());
//    public static final AddVelocityTrick ADD_VELOCITY = register("add_velocity", new AddVelocityTrick());
//    public static final PolymorphTrick POLYMORPH = register("polymorph", new PolymorphTrick());
//    public static final DispelPolymorphTrick DISPEL_POLYMORPH = register("dispel_polymorph", new DispelPolymorphTrick());

    // Math
    public static final AddTrick ADD = register("add", new AddTrick());
    public static final SubtractTrick SUBTRACT = register("subtract", new SubtractTrick());
    public static final MultiplyTrick MULTIPLY = register("multiply", new MultiplyTrick());
    public static final DivideTrick DIVIDE = register("divide", new DivideTrick());
    public static final ModuloTrick MODULO = register("modulo", new ModuloTrick());
    public static final FloorTrick FLOOR = register("floor", new FloorTrick());
    public static final CeilTrick CEIL = register("ceil", new CeilTrick());
    public static final RoundTrick ROUND = register("round", new RoundTrick());
    public static final MaxTrick MAX = register("max", new MaxTrick());
    public static final MinTrick MIN = register("min", new MinTrick());
    public static final SqrtTrick SQRT = register("sqrt", new SqrtTrick());

    // Vector
    public static final ExtractXTrick EXTRACT_X = register("extract_x", new ExtractXTrick());
    public static final ExtractYTrick EXTRACT_Y = register("extract_y", new ExtractYTrick());
    public static final ExtractZTrick EXTRACT_Z = register("extract_z", new ExtractZTrick());
    public static final LengthTrick LENGTH = register("length", new LengthTrick());
    public static final MergeVectorTrick MERGE_VECTOR = register("merge_vector", new MergeVectorTrick());

    // Boolean
    public static final IfElseTrick IF_ELSE = register("if_else", new IfElseTrick());
    public static final EqualsTrick EQUALS = register("equals", new EqualsTrick());
    public static final NotEqualsTrick NOT_EQUALS = register("not_equals", new NotEqualsTrick());
    public static final AllTrick ALL = register("all", new AllTrick());
    public static final AnyTrick ANY = register("any", new AnyTrick());
    public static final NoneTrick NONE = register("none", new NoneTrick());

    // List
    public static final ListAddTrick LIST_ADD = register("list_add", new ListAddTrick());
    public static final ListAddRangeTrick LIST_ADD_RANGE = register("list_add_range", new ListAddRangeTrick());
    public static final ListCreateTrick LIST_CREATE = register("list_create", new ListCreateTrick());
    public static final ListGetTrick LIST_GET = register("list_get", new ListGetTrick());
    public static final ListIndexOfTrick LIST_INDEX_OF = register("list_index_of", new ListIndexOfTrick());
    public static final ListInsertTrick LIST_INSERT = register("list_insert", new ListInsertTrick());
    public static final ListRemoveElementTrick LIST_REMOVE_ELEMENT = register("list_remove_element", new ListRemoveElementTrick());
    public static final ListRemoveTrick LIST_REMOVE = register("list_remove", new ListRemoveTrick());

    // Events
    public static final CreateSpellCircleTrick CREATE_SPELL_CIRCLE = register("create_spell_circle", new CreateSpellCircleTrick());
    public static final DeleteSpellCircleTrick DELETE_SPELL_CIRCLE = register("delete_spell_circle", new DeleteSpellCircleTrick());

    // Blocks
    public static final SwapBlockTrick SWAP_BLOCK = register("swap_block", new SwapBlockTrick());
//    public static final ConjureFlowerTrick CONJURE_FLOWER = register("conjure_flower", new ConjureFlowerTrick());
//    public static final ConjureWaterTrick CONJURE_WATER = register("conjure_water", new ConjureWaterTrick());
    public static final CheckBlockTrick CHECK_BLOCK = register("check_block", new CheckBlockTrick());
//    public static final DestabilizeBlockTrick DESTABILIZE_BLOCK = register("destabilize_block", new DestabilizeBlockTrick());

    private static <T extends Trick> T register(String path, T trick) {
        return Registry.register(REGISTRY, Trickster.id(path), trick);
    }

    @Nullable
    public static Trick lookup(Pattern pattern) {
        return LOOKUP.get(pattern);
    }

    public static void register() {
        // init the class :brombeere:
    }
}
