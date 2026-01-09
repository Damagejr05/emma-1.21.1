package net.damagejr05.emma.item;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;


public class EmmaMask extends TrinketItem {
    public EmmaMask(Settings settings) {
        super(settings);
    }

    public static boolean isMaskEquipped(PlayerEntity player) {
        return TrinketsApi.getTrinketComponent(player).map(component -> component.isEquipped(ModItems.EMMA_MASK)).orElse(false);
    }

    public void maskInvsibility(PlayerEntity player){
        if (!player.getWorld().isClient() && isMaskEquipped(player)){
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 210, 0, false,true, false));



        }
    }

    }



