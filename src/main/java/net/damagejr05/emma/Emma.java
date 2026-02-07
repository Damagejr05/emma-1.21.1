package net.damagejr05.emma;

import dev.emi.trinkets.api.event.TrinketDropCallback;
import net.damagejr05.emma.entity.ModEntities;
import net.damagejr05.emma.item.tracker.ShadowstepTracker;
import net.damagejr05.emma.entity.TeleportPositionEntity;
import net.damagejr05.emma.item.ModItemGroups;
import net.damagejr05.emma.item.ModItems;
import net.damagejr05.emma.util.ModSounds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Emma implements ModInitializer {
	public  static  final  String MOD_ID = "emma";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItems.registerModItems();
		ModItemGroups.registerItemGroups();
		ModEntities.registerModEntities();
		ModSounds.registerSounds();
		TrinketDropCallback.EVENT.register((dropRule, itemStack, slotReference, livingEntity) -> {
			Item item = itemStack.getItem();
			if (item == ModItems.EMMA_MASK) {
				return dropRule.DESTROY;
			}
            return dropRule;
        });

		ServerLivingEntityEvents.AFTER_DAMAGE.register(
				(entity, world, source, amount, blocked) -> {
					if (!(entity instanceof PlayerEntity player)) return;

					TeleportPositionEntity anchor = ShadowstepTracker.get(player);
					if (anchor != null && !anchor.isRemoved()) {
						anchor.reduceLifetimeByDamage(amount);
					}
				}
		);
	}
}