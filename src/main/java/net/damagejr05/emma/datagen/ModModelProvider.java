package net.damagejr05.emma.datagen;

import net.damagejr05.emma.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ModItems.EXPLOSIVE_EXIT, Models.HANDHELD);
        itemModelGenerator.register(ModItems.OBSCURING_HAZE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.WARDENS_INSPIRATION, Models.HANDHELD);
        itemModelGenerator.register(ModItems.SHADOWSTEP, Models.HANDHELD);
        itemModelGenerator.register(ModItems.GRAPPLING_BLADE, Models.HANDHELD);
    }
}