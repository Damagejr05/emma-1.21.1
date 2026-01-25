package net.damagejr05.emma.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;

public final class GetawayPlanHelper {

    private GetawayPlanHelper() {}

    public static int getGetawayPlanCount(ItemStack containerStack, Item getawayPlan) {
        BundleContentsComponent contents =
                containerStack.get(DataComponentTypes.BUNDLE_CONTENTS);

        if (contents == null) return 0;

        int count = 0;
        for (ItemStack stack : contents.iterate()) {
            if (stack.isOf(getawayPlan)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static boolean hasGetawayPlan(ItemStack containerStack, Item getawayPlan) {
        return getGetawayPlanCount(containerStack, getawayPlan) > 0;
    }

    public static int getTaggedGetawayPlanCount(ItemStack containerStack, TagKey<Item> tag) {
        BundleContentsComponent contents =
                containerStack.get(DataComponentTypes.BUNDLE_CONTENTS);

        if (contents == null) return 0;

        int count = 0;
        for (ItemStack stack : contents.iterate()) {
            if (stack.isIn(tag)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    public static boolean hasTaggedGetawayPlan(ItemStack containerStack, TagKey<Item> tag) {
        return getTaggedGetawayPlanCount(containerStack, tag) > 0;
    }
}
