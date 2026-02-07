package net.damagejr05.emma.datagen;

import net.damagejr05.emma.item.ModItems;
import net.damagejr05.emma.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider<Item> {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, RegistryKeys.ITEM, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(ModTags.Items.GETAWAY_PLANS)
                .add(ModItems.EXPLOSIVE_EXIT)
                .add(ModItems.OBSCURING_HAZE)
                .add(ModItems.WARDENS_INSPIRATION)
                .add(ModItems.SHADOWSTEP)
                .add(ModItems.GRAPPLING_BLADE)
                ;
    }
}
