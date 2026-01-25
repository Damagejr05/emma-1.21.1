package net.damagejr05.emma.client.render;

import net.damagejr05.emma.entity.TeleportPositionEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class EmptyRenderer extends EntityRenderer<TeleportPositionEntity> {

    public EmptyRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(
            TeleportPositionEntity entity,
            float yaw,
            float tickDelta,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light
    ){

    }

    @Override
    public Identifier getTexture(TeleportPositionEntity entity) {
        return null;
    }
}
