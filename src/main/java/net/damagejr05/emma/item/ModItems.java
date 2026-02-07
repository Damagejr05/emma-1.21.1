package net.damagejr05.emma.item;

import net.damagejr05.emma.Emma;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item EMMA_MASK = registerItem("emma_mask", new EmmaMask(new Item.Settings().maxCount(1)));
    public static final Item EMMA_HELMET = registerItem("emma_helmet",
            new EmmaArmorItem(ModArmorMaterials.EMMA_ARMOR_MATERIAL, ArmorItem.Type.HELMET,
                    new Item.Settings().maxDamage(ArmorItem.Type.HELMET.getMaxDamage(25))));
    public static final Item EMMA_CHESTPLATE = registerItem("emma_chestplate",
            new EmmaArmorItem(ModArmorMaterials.EMMA_ARMOR_MATERIAL, ArmorItem.Type.CHESTPLATE,
                    new Item.Settings().maxDamage(ArmorItem.Type.CHESTPLATE.getMaxDamage(25))));
    public static final Item EMMA_LEGGINGS = registerItem("emma_leggings",
            new EmmaArmorItem(ModArmorMaterials.EMMA_ARMOR_MATERIAL, ArmorItem.Type.LEGGINGS,
                    new Item.Settings().maxDamage(ArmorItem.Type.LEGGINGS.getMaxDamage(25))));
    public static final Item EMMA_BOOTS = registerItem("emma_boots",
            new EmmaArmorItem(ModArmorMaterials.EMMA_ARMOR_MATERIAL, ArmorItem.Type.BOOTS,
                    new Item.Settings().maxDamage(ArmorItem.Type.BOOTS.getMaxDamage(25))));
    public static final Item EMMA_BLADE = registerItem("emma_blade",
            new EmmaBlade(ModToolMaterials.EMMA_MATERIAL,
                    4, -2.4f, (new Item.Settings()
                            .component(
                                    DataComponentTypes.BUNDLE_CONTENTS,
                                    BundleContentsComponent.DEFAULT
                            ).attributeModifiers(SwordItem.createAttributeModifiers(ModToolMaterials.EMMA_MATERIAL,
                            3, -2.4F)))));
    public static final Item EXPLOSIVE_EXIT = registerItem("explosive_exit",
            new GetawayActItem(new Item.Settings().maxCount(1).maxDamage(250).attributeModifiers(GetawayActItem.createAttributeModifiers())));
    public static final Item OBSCURING_HAZE = registerItem("obscuring_haze",
            new GetawayActItem(new Item.Settings().maxCount(1).maxDamage(250).attributeModifiers(GetawayActItem.createAttributeModifiers())));
    public static final Item WARDENS_INSPIRATION = registerItem("wardens_inspiration",
            new GetawayActItem(new Item.Settings().maxCount(1).maxDamage(250).attributeModifiers(GetawayActItem.createAttributeModifiers())));
    public static final Item SHADOWSTEP = registerItem("shadowstep",
            new GetawayActItem(new Item.Settings().maxCount(1).maxDamage(250).attributeModifiers(GetawayActItem.createAttributeModifiers())));
    public static final Item GRAPPLING_BLADE = registerItem("grappling_blade",
            new GetawayActItem(new Item.Settings().maxCount(1).maxDamage(250).attributeModifiers(GetawayActItem.createAttributeModifiers())));
    public static final Item GRAPPLING_BLADE_ENTITY = registerItem("grappling_blade_entity",
            new Item(new Item.Settings()));


    public static final Item SMOKE_BOMB = registerItem("smoke_bomb",
            new SmokeBombItem(new Item.Settings().maxCount(16)));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(Emma.MOD_ID, name), item);
    }

    public static void  registerModItems() {
        Emma.LOGGER.info("Registering Mod Items for " + Emma.MOD_ID);

    }
}
