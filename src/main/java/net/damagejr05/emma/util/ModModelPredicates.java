package net.damagejr05.emma.util;

import net.damagejr05.emma.Emma;
import net.damagejr05.emma.item.tracker.GrappleTracker;
import net.damagejr05.emma.item.tracker.ShadowstepTracker;
import net.damagejr05.emma.item.ModItems;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ModModelPredicates {
    public static void registerModelPredicates() {
        ModelPredicateProviderRegistry.register(
                ModItems.EMMA_BLADE,
                Identifier.of(Emma.MOD_ID, "explosive_exit"),
                (stack, world, entity, seed) ->
                        contains(stack, ModItems.EXPLOSIVE_EXIT) ? 1.0f : 0.0f
        );

        ModelPredicateProviderRegistry.register(
                ModItems.EMMA_BLADE,
                Identifier.of(Emma.MOD_ID, "obscuring_haze"),
                (stack, world, entity, seed) ->
                        contains(stack, ModItems.OBSCURING_HAZE) ? 1.0f : 0.0f
        );

        ModelPredicateProviderRegistry.register(
                ModItems.EMMA_BLADE,
                Identifier.of(Emma.MOD_ID, "wardens_inspiration"),
                (stack, world, entity, seed) ->
                        contains(stack, ModItems.WARDENS_INSPIRATION) ? 1.0f : 0.0f
        );

        ModelPredicateProviderRegistry.register(
                ModItems.EMMA_BLADE,
                Identifier.of(Emma.MOD_ID, "shadowstep"),
                (stack, world, entity, seed) ->
                        contains(stack, ModItems.SHADOWSTEP) ? 1.0f : 0.0f
        );

        ModelPredicateProviderRegistry.register(
                ModItems.EMMA_BLADE,
                Identifier.of(Emma.MOD_ID, "shadowstep_active"),
                (stack, world, entity, seed) ->
                        (entity instanceof PlayerEntity player && ShadowstepTracker.has(player) && contains(stack, ModItems.SHADOWSTEP)) ? 1.0F : 0.0F
        );

        ModelPredicateProviderRegistry.register(
                ModItems.EMMA_BLADE,
                Identifier.of(Emma.MOD_ID, "grappling_blade"),
                (stack, world, entity, seed) -> {
                    if (entity instanceof PlayerEntity player && GrappleTracker.has(player)) return 0.0F;
                    return contains(stack, ModItems.GRAPPLING_BLADE) ? 1.0F : 0.0F;
                }
        );

        ModelPredicateProviderRegistry.register(
                ModItems.EMMA_BLADE,
                Identifier.of(Emma.MOD_ID, "grapple_fired"),
                (stack, world, entity, seed) ->
                        (entity instanceof PlayerEntity player && GrappleTracker.has(player) && contains(stack, ModItems.GRAPPLING_BLADE)) ? 1.0F : 0.0F
        );

        ModelPredicateProviderRegistry.register(
                ModItems.EMMA_BLADE,
                Identifier.of(Emma.MOD_ID, "active"),
                (stack, world, entity, seed) ->
                        entity instanceof PlayerEntity player
                                && player.isUsingItem()
                                && player.getActiveItem() == stack
                                && contains(stack, ModItems.OBSCURING_HAZE)
                                ? 1.0F : 0.0F
        );
    }


    private static boolean contains(ItemStack stack, Item item) {
        BundleContentsComponent contents =
                stack.get(DataComponentTypes.BUNDLE_CONTENTS);

        if (contents == null) return false;

        for (ItemStack inner : contents.iterate()) {
            if (inner.isOf(item)) return true;
        }
        return false;
    }

}