package net.damagejr05.emma.client.render;

import net.damagejr05.emma.entity.GrapplingBladeEntity;
import net.damagejr05.emma.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GrapplingBladeRenderer extends EntityRenderer<GrapplingBladeEntity> {
    private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/fishing_hook.png");
    private static final RenderLayer LAYER;
    private final ItemRenderer itemRenderer;

    public GrapplingBladeRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(GrapplingBladeEntity entity, float yaw, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider consumers, int light) {
        PlayerEntity player = entity.getPlayerOwner();
        if (player == null) return;

        matrices.push();

        ItemStack stack = new ItemStack(ModItems.GRAPPLING_BLADE_ENTITY);

        matrices.scale(1.5F, 1.5F, 1.5F);
        yaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw());
        float pitch = MathHelper.lerp(tickDelta, entity.prevPitch, entity.getPitch());

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(yaw - 90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(pitch));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-45.0F));
        matrices.translate(0f, -0.5f, 0f);


        MinecraftClient.getInstance().getItemRenderer().renderItem(
                stack,
                ModelTransformationMode.GROUND,
                light,
                OverlayTexture.DEFAULT_UV,
                matrices,
                consumers,
                entity.getWorld(),
                entity.getId()
        );

        matrices.pop();

        float h = player.getHandSwingProgress(tickDelta);
        float j = MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);

        Vec3d handPos = this.getHandPos(player, j, tickDelta);
        Vec3d hookPos = entity.getLerpedPos(tickDelta).add(0.0F, 0.25F, 0.0F);

        float dx = (float)(handPos.x - hookPos.x);
        float dy = (float)(handPos.y - hookPos.y);
        float dz = (float)(handPos.z - hookPos.z);

        VertexConsumer lineBuffer = consumers.getBuffer(RenderLayer.getLineStrip());
        MatrixStack.Entry entry = matrices.peek();

        for (int seg = 0; seg <= 16; ++seg) {
            renderFishingLine(
                    dx, dy, dz,
                    lineBuffer,
                    entry,
                    percentage(seg, 16),
                    percentage(seg + 1, 16)
            );
        }

        super.render(entity, yaw, tickDelta, matrices, consumers, light);
    }


    private Vec3d getHandPos(PlayerEntity player, float f, float tickDelta) {
        int i = player.getMainArm() == Arm.RIGHT ? 1 : -1;
        ItemStack itemStack = player.getMainHandStack();
        if (!itemStack.isOf(ModItems.EMMA_BLADE)) {
            i = -i;
        }

        if (this.dispatcher.gameOptions.getPerspective().isFirstPerson() && player == MinecraftClient.getInstance().player) {
            double m = (double)960.0F / (double)(Integer)this.dispatcher.gameOptions.getFov().getValue();
            Vec3d vec3d = this.dispatcher.camera.getProjection().getPosition((float)i * 0.525F, -0.1F).multiply(m).rotateY(f * 0.5F).rotateX(-f * 0.7F);
            return player.getCameraPosVec(tickDelta).add(vec3d);
        } else {
            float g = MathHelper.lerp(tickDelta, player.prevBodyYaw, player.bodyYaw) * ((float)Math.PI / 180F);
            double d = (double)MathHelper.sin(g);
            double e = (double)MathHelper.cos(g);
            float h = player.getScale();
            double j = (double)i * 0.35 * (double)h;
            double k = 0.65 * (double)h;
            float l = player.isInSneakingPose() ? -0.1875F : 0.0F;
            return player.getCameraPosVec(tickDelta).add(-e * j - d * k, (double)l - 0.6 * (double)h, -d * j + e * k);
        }
    }

    private static float percentage(int value, int max) {
        return (float)value / (float)max;
    }

    private static void vertex(VertexConsumer buffer, MatrixStack.Entry matrix, int light, float x, int y, int u, int v) {
        buffer.vertex(matrix, x - 0.5F, (float)y - 0.5F, 0.0F).color(-1).texture((float)u, (float)v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(matrix, 0.0F, 1.0F, 0.0F);
    }

    private static void renderFishingLine(float x, float y, float z, VertexConsumer buffer, MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {
        float f = x * segmentStart;
        float g = y * (segmentStart * segmentStart + segmentStart) * 0.5F + 0.25F;
        float h = z * segmentStart;
        float i = x * segmentEnd - f;
        float j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5F + 0.25F - g;
        float k = z * segmentEnd - h;
        float l = MathHelper.sqrt(i * i + j * j + k * k);
        i /= l;
        j /= l;
        k /= l;
        buffer.vertex(matrices, f, g, h).color(0xFF707070).normal(matrices, i, j, k);
    }

    private static Vec3d rotateOffset(Vec3d offset, float yawDeg, float pitchDeg) {
        float yaw = -yawDeg * MathHelper.RADIANS_PER_DEGREE;
        float pitch = pitchDeg * MathHelper.RADIANS_PER_DEGREE;

        double cosYaw = Math.cos(yaw);
        double sinYaw = Math.sin(yaw);

        double x1 = offset.x * cosYaw - offset.z * sinYaw;
        double z1 = offset.x * sinYaw + offset.z * cosYaw;

        double cosPitch = Math.cos(pitch);
        double sinPitch = Math.sin(pitch);

        double y2 = offset.y * cosPitch - z1 * sinPitch;
        double z2 = offset.y * sinPitch + z1 * cosPitch;

        return new Vec3d(x1, y2, z2);
    }

    public Identifier getTexture(GrapplingBladeEntity grapplingBladeEntity) {
        return TEXTURE;
    }

    @Override
    public boolean shouldRender(GrapplingBladeEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }

    static {
        LAYER = RenderLayer.getEntityCutout(TEXTURE);
    }
}
