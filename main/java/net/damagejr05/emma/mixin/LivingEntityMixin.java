package net.damagejr05.emma.mixin;

import net.damagejr05.emma.item.tracker.ExplosiveExitTracker;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V", ordinal = 3, shift = At.Shift.AFTER))
    public void airborne(Vec3d movementInput, CallbackInfo ci) {
        if ((Object)this instanceof PlayerEntity player
                && ExplosiveExitTracker.has(player)
                && !player.isOnGround()) {

            player.setVelocity(
                    player.getVelocity().add(
                            airMovement(movementInput, player.getHeadYaw()).multiply(0.05)
                    )
            );
        }
    }

    @Unique
    public Vec3d airMovement(Vec3d movementInput, float yaw) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        } else {
            Vec3d vec3d = (d > (double)1.0F ? movementInput.normalize() : movementInput);
            float f = MathHelper.sin(yaw * ((float)Math.PI / 180F));
            float g = MathHelper.cos(yaw * ((float)Math.PI / 180F));
            return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
        }
    }
}
