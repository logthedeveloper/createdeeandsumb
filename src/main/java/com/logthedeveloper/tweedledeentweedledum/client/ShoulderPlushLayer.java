package com.logthedeveloper.tweedledeentweedledum.client;

import com.logthedeveloper.tweedledeentweedledum.Config;
import com.logthedeveloper.tweedledeentweedledum.CuriosCompat;
import com.logthedeveloper.tweedledeentweedledum.ModAttachments;
import com.logthedeveloper.tweedledeentweedledum.ModItems;
import com.logthedeveloper.tweedledeentweedledum.PlushType;
import com.logthedeveloper.tweedledeentweedledum.ShoulderData;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.EnumMap;
import java.util.Map;


public class ShoulderPlushLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private final Map<PlushType, ItemStack> stacks = new EnumMap<>(PlushType.class);

    public ShoulderPlushLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
                       float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {
        if (player.isSpectator() || player.isInvisible()) {
            return;
        }
        ShoulderData data = player.getData(ModAttachments.SHOULDER);
        boolean leftOccupied = false;
        boolean rightOccupied = false;


        for (ItemStack curiosStack : CuriosCompat.getTweedleStacks(player)) {
            if (!rightOccupied) {
                renderPlush(poseStack, buffer, packedLight, player, curiosStack, false);
                rightOccupied = true;
            } else if (!leftOccupied) {
                renderPlush(poseStack, buffer, packedLight, player, curiosStack, true);
                leftOccupied = true;
            }
            if (leftOccupied && rightOccupied) {
                break;
            }
        }


        if (data.right() != null && !rightOccupied) {
            renderPlush(poseStack, buffer, packedLight, player,
                    stacks.computeIfAbsent(data.right(), t -> new ItemStack(ModItems.itemFor(t))), false);
            rightOccupied = true;
        }
        if (data.left() != null && !leftOccupied) {
            renderPlush(poseStack, buffer, packedLight, player,
                    stacks.computeIfAbsent(data.left(), t -> new ItemStack(ModItems.itemFor(t))), true);
            leftOccupied = true;
        }
    }

    private void renderPlush(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                             AbstractClientPlayer player, ItemStack stack, boolean leftShoulder) {

        float scale = Config.SHOULDER_SCALE.get().floatValue();
        double offsetX = Config.SHOULDER_OFFSET_X.get();
        double offsetY = Config.SHOULDER_OFFSET_Y.get();
        double offsetZ = Config.SHOULDER_OFFSET_Z.get();

        poseStack.pushPose();


        double side = leftShoulder ? 1.0D : -1.0D;
        double x = side * (0.36D + offsetX);
        double y = (player.isCrouching() ? 0.2D : 0.0D) - offsetY;

        poseStack.translate(x, y - 0.5D * scale, -offsetZ);


        poseStack.scale(-scale, -scale, scale);

        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.NONE, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, buffer, player.level(), player.getId());

        poseStack.popPose();
    }
}
