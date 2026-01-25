package net.damagejr05.emma.item;

import net.damagejr05.emma.entity.SmokeBombProjectileEntity;
import net.damagejr05.emma.util.ModSounds;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.ProjectileItem.Settings;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SmokeBombItem extends Item implements ProjectileItem {

    private static final int COOLDOWN = 20;

    public SmokeBombItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (!world.isClient()) {
            SmokeBombProjectileEntity entity =
                    new SmokeBombProjectileEntity(world,
                            user.getPos().getX(),
                            user.getEyePos().getY(),
                            user.getPos().getZ(),
                            user
                    );

            entity.setItem(stack);
            entity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 0.75F, 1.0F);
            world.spawnEntity(entity);
        }

        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                ModSounds.SMOKE_BOMB_THROW,
                SoundCategory.NEUTRAL,
                0.5F,
                1.0F
        );

        user.getItemCooldownManager().set(this, COOLDOWN);
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        stack.decrementUnlessCreative(1, user);

        return TypedActionResult.success(stack, world.isClient());
    }


    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        Random random = world.getRandom();

        Vec3d velocity = new Vec3d(
                random.nextTriangular(direction.getOffsetX(), 0.1),
                random.nextTriangular(direction.getOffsetY(), 0.1),
                random.nextTriangular(direction.getOffsetZ(), 0.1)
        );

        SmokeBombProjectileEntity entity =
                new SmokeBombProjectileEntity(world, pos.getX(), pos.getY(), pos.getZ(), null);

        entity.setItem(stack);
        entity.setVelocity(velocity);
        return entity;
    }

    @Override
    public void initializeProjectile(
            ProjectileEntity entity,
            double x,
            double y,
            double z,
            float power,
            float uncertainty
    ) {
        // Not needed — matching WindChargeItem
    }

    @Override
    public ProjectileItem.Settings getProjectileSettings() {
        return ProjectileItem.Settings.builder()
                .positionFunction((pointer, facing) ->
                        DispenserBlock.getOutputLocation(pointer, 1.0F, Vec3d.ZERO))
                .power(0.75F)
                .uncertainty(6.0F)
                .build();
    }
}
