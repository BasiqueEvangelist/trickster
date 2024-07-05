package dev.enjarai.trickster.spell.tricks.tree;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.AddressNotInTreeBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.IncorrectFragmentBlunder;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AddLeafNodeTrick extends Trick {
    public AddLeafNodeTrick() {
        super(Pattern.of(4, 6, 7, 8, 2, 1, 0, 4, 3));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, SpellPart.class, 0);
        var addressFragment = expectInput(fragments, ListFragment.class, 1);

        var address = sanitizeAddress(addressFragment);
        var newSpell = spell.deepClone();

        var node = newSpell;
        for (int index : address) {
            var subParts = node.subParts;
            if (subParts.size() > index && subParts.get(index).isPresent()) {
                node = subParts.get(index).get();
            } else {
                throw new AddressNotInTreeBlunder(this, address);
            }
        }
        node.subParts.add(Optional.of(new SpellPart(new PatternGlyph(), List.of())));

        return newSpell;
    }

    private List<Integer> sanitizeAddress(ListFragment address) {
        var sanitizedAddress = new ArrayList<Integer>();

        for (Fragment fragment : address.fragments()) {
            if (fragment instanceof NumberFragment index && index.isInteger()) {
                sanitizedAddress.add((int) index.number());
            } else {
                throw new IncorrectFragmentBlunder(
                        this,
                        1,
                        Text.translatable(Trickster.MOD_ID + ".fragment." + Trickster.MOD_ID + "." + "integer_list"),
                        address);
            }
        }

        return sanitizedAddress;
    }
}
