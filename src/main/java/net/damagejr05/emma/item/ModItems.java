package net.damagejr05.emma.item;

import net.damagejr05.emma.Emma;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item EMMA_MASK = registerItem("emma_mask", new EmmaMask(new Item.Settings().maxCount(1)));


    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(Emma.MOD_ID, name), item);
    }



    public static void  registerModItems() {
        Emma.LOGGER.info("Registering Mod Items for " + Emma.MOD_ID);

    }
}
