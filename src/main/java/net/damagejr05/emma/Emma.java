package net.damagejr05.emma;

import dev.emi.trinkets.api.TrinketEnums;
import dev.emi.trinkets.api.event.TrinketDropCallback;
import net.damagejr05.emma.item.ModItems;
import net.fabricmc.api.ModInitializer;

import net.minecraft.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Emma implements ModInitializer {
	public  static  final  String MOD_ID = "emma";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		TrinketDropCallback.EVENT.register((dropRule, itemStack, slotReference, livingEntity) -> {
			Item item = itemStack.getItem();
			if (item == ModItems.EMMA_MASK) {
				return dropRule.DESTROY;
			}
            return dropRule;
        });
	}
}