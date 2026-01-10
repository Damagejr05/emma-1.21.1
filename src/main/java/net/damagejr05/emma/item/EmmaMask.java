package net.damagejr05.emma.item;


import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.item.ItemStack;


public class EmmaMask extends TrinketItem {
    public EmmaMask(Settings settings) {
        super(settings);
    }


    @Override
    public void tick(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.tick(stack, slot, entity);
    if (entity.getWorld().isClient || !(entity instanceof PlayerEntity player)){
            return;
        }
        TrinketsApi.getTrinketComponent(player).ifPresent(trinketComponent -> {
            boolean wearingMask = !trinketComponent.getEquipped(ModItems.EMMA_MASK).isEmpty();
            if (wearingMask) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 20, 0, false, true, false));
            } else {
                player.removeStatusEffect(StatusEffects.INVISIBILITY);
            }

        });}}