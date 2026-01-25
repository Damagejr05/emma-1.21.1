package net.damagejr05.emma.entity;

import net.damagejr05.emma.Emma;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<SmokeBombProjectileEntity> SMOKE_BOMB = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Emma.MOD_ID, "smoke_bomb"),
            FabricEntityTypeBuilder.<SmokeBombProjectileEntity>create(SpawnGroup.MISC, SmokeBombProjectileEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f))
                    .trackRangeBlocks(80)
                    .trackedUpdateRate(3)
                    .build()
    );

    public static final EntityType<GrapplingBladeEntity> GRAPPLING_BLADE = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Emma.MOD_ID, "grappling_blade"),
            FabricEntityTypeBuilder.<GrapplingBladeEntity>create(SpawnGroup.MISC, GrapplingBladeEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 1f))
                    .trackRangeBlocks(80)
                    .trackedUpdateRate(3)
                    .build()
    );

    public static final EntityType<TeleportPositionEntity> SHADOWSTEP_TELEPORT_POINT = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(Emma.MOD_ID, "shadowstep_teleport_point"),
            FabricEntityTypeBuilder.<TeleportPositionEntity>create(SpawnGroup.MISC, TeleportPositionEntity::new)
                    .dimensions(EntityDimensions.fixed(1f, 2f))
                    .trackRangeChunks(2048)
                    .trackedUpdateRate(3)
                    .build()
    );

    public static void registerModEntities() {
        System.out.println("Registering entities for " + Emma.MOD_ID);
    }

}
