package net.damagejr05.emma.item;


import dev.emi.trinkets.api.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;


public class EmmaMask extends TrinketItem {
    public EmmaMask(Settings settings) {
        super(settings);
    }




    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);

        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player){
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 0, false, true, false));
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity){
        super.onUnequip(stack, slot, entity);

        if (!entity.getWorld().isClient && entity instanceof PlayerEntity player) {
            player.removeStatusEffect(StatusEffects.INVISIBILITY);
        }    }


    public static boolean isMaskEquipped(PlayerEntity player) {
        return TrinketsApi.getTrinketComponent(player).map(component -> component.isEquipped(ModItems.EMMA_MASK)).orElse(false);
    }

}



