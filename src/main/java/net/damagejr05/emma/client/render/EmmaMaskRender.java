package net.damagejr05.emma.client.render;

import dev.emi.trinkets.api.client.TrinketRenderer;
import dev.emi.trinkets.api.SlotReference;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class EmmaMaskRender implements TrinketRenderer {

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {


        // Only render for players
        if (!(entity instanceof AbstractClientPlayerEntity player)) return;
        if (!(contextModel instanceof PlayerEntityModel<?> playerModel)) return;

        matrices.push();

        int overlay = OverlayTexture.DEFAULT_UV;

        //Moves the mask when crouching
        matrices.translate(0, playerModel.head.pivotY/16f, 0);

        if (player.isSwimming() || player.isCrawling() || player.isFallFlying()) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(playerModel.getHead().roll));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(headYaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-45f));
        }
        else {


            //Rotates the mask according to player's looking direction
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(headYaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(headPitch));
        }

        //For some reason it was upside down so this flips it over
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));

        //Controls the position
        matrices.translate(0.0, 0.15, 0.3);

        //Controls the size
        matrices.scale(0.7f, 0.7f, 0.7f);

        //Renders the mask
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        itemRenderer.renderItem(stack, ModelTransformationMode.NONE, light, overlay, matrices, vertexConsumers, player.getWorld(),
                0
        );

        matrices.pop();
    }
}
