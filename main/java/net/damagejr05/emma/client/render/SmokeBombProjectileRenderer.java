package net.damagejr05.emma.client.render;

import net.damagejr05.emma.entity.SmokeBombProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class SmokeBombProjectileRenderer extends EntityRenderer<SmokeBombProjectileEntity> {
    private final ItemRenderer itemRenderer;
    public SmokeBombProjectileRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(SmokeBombProjectileEntity smokeBombProjectileEntity,
                       float yaw,
                       float tickDelta,
                       MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumers,
                       int light) {
        matrixStack.push();

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, smokeBombProjectileEntity.prevYaw, smokeBombProjectileEntity.getYaw())));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(MathHelper.lerp(tickDelta, smokeBombProjectileEntity.prevPitch, smokeBombProjectileEntity.getPitch())));

        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(0.0F));

        float rotationX = smokeBombProjectileEntity.getRotationX();
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotationX));

        float rotationY = smokeBombProjectileEntity.getRotationY();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotationY));

        matrixStack.scale(1.75F, 1.75F, 1.75F);

        matrixStack.translate(0.05F, -0.0F, 0.0F);

        ItemStack BaseSmokeBombItem = smokeBombProjectileEntity.getStack();
        if (BaseSmokeBombItem.isEmpty()) {
            matrixStack.pop();
            super.render(smokeBombProjectileEntity, yaw, tickDelta, matrixStack, vertexConsumers, light);
            return;
        }

        this.itemRenderer.renderItem(BaseSmokeBombItem, net.minecraft.client.render.model.json.ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumers, smokeBombProjectileEntity.getWorld(), 0);

        matrixStack.pop();
        super.render(smokeBombProjectileEntity, yaw, tickDelta, matrixStack, vertexConsumers, light);
    }

    @Override
    public Identifier getTexture(SmokeBombProjectileEntity entity) {
        return null;
    }
}
