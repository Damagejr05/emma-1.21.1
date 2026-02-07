package net.damagejr05.emma.util;

import net.damagejr05.emma.Emma;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Items {

        public static final TagKey<Item> GETAWAY_PLANS =
                createTag("getaway_plans");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(Emma.MOD_ID, name));
        }

    }
}
