package net.damagejr05.emma;

import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import net.damagejr05.emma.client.render.EmmaMaskRender;
import net.damagejr05.emma.item.ModItems;
import net.fabricmc.api.ClientModInitializer;

public class EmmaClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        TrinketRendererRegistry.registerRenderer(ModItems.EMMA_MASK, (TrinketRenderer) new EmmaMaskRender());

    }
}
