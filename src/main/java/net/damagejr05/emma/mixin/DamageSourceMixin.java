package net.damagejr05.emma.mixin;

import dev.emi.trinkets.api.TrinketsApi;
import net.damagejr05.emma.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageSource.class)
public abstract class DamageSourceMixin {

	@Inject(method = "getDeathMessage", at = @At("RETURN"), cancellable = true)
	private void replaceAttackerName(LivingEntity victim, CallbackInfoReturnable<Text> cir) {
		if (((DamageSource)(Object)this).getAttacker() instanceof PlayerEntity player) {
			TrinketsApi.getTrinketComponent(player).ifPresent(trinkets -> {
				if (!trinkets.getEquipped(ModItems.EMMA_MASK).isEmpty()) {
					cir.setReturnValue(Text.literal(
							cir.getReturnValue().getString().replace(player.getName().getString(), "EMMA")
					));
				}
			});
		}
	}
}
