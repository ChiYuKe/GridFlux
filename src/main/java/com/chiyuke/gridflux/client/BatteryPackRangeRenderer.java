package com.chiyuke.gridflux.client;

import com.chiyuke.gridflux.GridFlux;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = GridFlux.MOD_ID, value = Dist.CLIENT)
public class BatteryPackRangeRenderer {
    private static BlockPos center;
    private static int range;
    private static long endTime;

    public static void show(BlockPos pos, int radius) {
        center = pos;
        range = Math.max(1, radius);
        endTime = System.currentTimeMillis() + 6000L;
    }

    @SubscribeEvent
    public static void render(RenderLevelStageEvent event) {
        if (center == null || System.currentTimeMillis() > endTime || event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }

        Vec3 camera = event.getCamera().getPosition();
        AABB box = new AABB(
                center.getX() + 0.5D - range,
                center.getY() + 0.5D - range,
                center.getZ() + 0.5D - range,
                center.getX() + 0.5D + range,
                center.getY() + 0.5D + range,
                center.getZ() + 0.5D + range
        );

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.translate(-camera.x, -camera.y, -camera.z);
        MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(poseStack, consumer, box, 1.0F, 0.0F, 0.0F, 0.65F);
        bufferSource.endBatch(RenderType.lines());
        poseStack.popPose();
    }
}
