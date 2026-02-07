package net.damagejr05.emma;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.damagejr05.emma.client.hud.ShadowstepHudRenderer;
import net.damagejr05.emma.client.render.EmmaMaskRender;
import net.damagejr05.emma.client.render.EmptyRenderer;
import net.damagejr05.emma.client.render.GrapplingBladeRenderer;
import net.damagejr05.emma.client.render.SmokeBombProjectileRenderer;
import net.damagejr05.emma.entity.ModEntities;
import net.damagejr05.emma.item.ModItems;
import net.damagejr05.emma.util.ModModelPredicates;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class EmmaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TrinketRendererRegistry.registerRenderer(ModItems.EMMA_MASK, new EmmaMaskRender());
        EntityRendererRegistry.register(ModEntities.SMOKE_BOMB, SmokeBombProjectileRenderer::new);
        EntityRendererRegistry.register(ModEntities.GRAPPLING_BLADE, GrapplingBladeRenderer::new);
        EntityRendererRegistry.register(ModEntities.SHADOWSTEP_TELEPORT_POINT, EmptyRenderer::new);

        ModModelPredicates.registerModelPredicates();
        ShadowstepHudRenderer.registerShadowstepHudRenderer();
    }
}
