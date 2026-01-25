package net.damagejr05.emma.item;

import net.minecraft.item.Item;
import net.minecraft.util.Formatting;

public class GetawayPlanTooltipHelper {

    public static Formatting getColor(Item item) {
        if (item == ModItems.EXPLOSIVE_EXIT) return Formatting.YELLOW;
        if (item == ModItems.OBSCURING_HAZE) return Formatting.LIGHT_PURPLE;
        if (item == ModItems.WARDENS_INSPIRATION) return Formatting.DARK_AQUA;
        if (item == ModItems.SHADOWSTEP) return Formatting.DARK_PURPLE;
        if (item == ModItems.GRAPPLING_BLADE) return Formatting.GOLD;

        return Formatting.WHITE;
    }

    private GetawayPlanTooltipHelper() {}
}
