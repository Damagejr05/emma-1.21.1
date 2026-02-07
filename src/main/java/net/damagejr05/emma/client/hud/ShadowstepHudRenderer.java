package net.damagejr05.emma.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import net.damagejr05.emma.Emma;
import net.damagejr05.emma.entity.TeleportPositionEntity;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class ShadowstepHudRenderer {
    private static final Identifier SHADOWSTEP_BAR = Identifier.of(Emma.MOD_ID, "textures/gui/shadowstep_bar.png");

    public static void registerShadowstepHudRenderer() { HudRenderCallback.EVENT.register
            ((context, tickDelta) -> { MinecraftClient client = MinecraftClient.getInstance();
                PlayerEntity player = client.player;

                if (player == null) return;
                TeleportPositionEntity anchor = findShadowstepEntity(player);
                if (anchor == null) return;
                int remaining = anchor.getRemainingTicksClient();

                int max = TeleportPositionEntity.MAX_LIFETIME;

                renderShadowstepBar(context, remaining, max);
            });
    }

    private static TeleportPositionEntity findShadowstepEntity(PlayerEntity player) {
        return player.getWorld()
                .getEntitiesByClass(TeleportPositionEntity.class, player.getBoundingBox().expand(2048),
                        e -> true ) .stream() .findFirst() .orElse(null);
    }

    private static void renderShadowstepBar(DrawContext context, int remaining, int max) {
        MinecraftClient client = MinecraftClient.getInstance();
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int barWidth = 42;
        int barHeight = 4;

        int x = (screenWidth / 2) - (barWidth / 2);
        int y = (screenHeight / 2) + 16;

        float alpha = 0.6f;

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);

        context.drawTexture(SHADOWSTEP_BAR, x, y, 0, 77, barWidth, barHeight);

        float progress = (float) remaining / (float) max;
        int foregroundWidth = MathHelper.clamp((int)(barWidth * progress), 0, barWidth);

        if (foregroundWidth > 0) {
            context.drawTexture(SHADOWSTEP_BAR, x, y, 0, 73, foregroundWidth, barHeight);
        }

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

}