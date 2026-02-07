package net.damagejr05.emma.item;

import net.damagejr05.emma.Emma;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup EMMA_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(Emma.MOD_ID, "emma_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.EMMA_MASK))
                    .displayName(Text.translatable("itemgroup.emma.emma_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.EMMA_MASK);
                        entries.add(ModItems.EMMA_BLADE);
                        entries.add(ModItems.EXPLOSIVE_EXIT);
                        entries.add(ModItems.OBSCURING_HAZE);
                        entries.add(ModItems.WARDENS_INSPIRATION);
                        entries.add(ModItems.SHADOWSTEP);
                        entries.add(ModItems.GRAPPLING_BLADE);
                        entries.add(ModItems.SMOKE_BOMB);
                    }).build());


    public static void registerItemGroups() {
        Emma.LOGGER.info("Registering Item Groups for " + Emma.MOD_ID);
    }
}
