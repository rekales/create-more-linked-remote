package com.krei.cmlinkedremote;

import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Axis;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxRenderer;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModel;
import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.PartialItemModelRenderer;
import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import dev.engine_room.flywheel.lib.transform.TransformStack;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.animation.LerpedFloat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class LinkedRemoteItemRenderer  extends CustomRenderedItemModelRenderer {
    protected static final PartialModel POWERED = PartialModel.of(ResourceLocation.fromNamespaceAndPath(LinkedRemote.MODID, "item/linked_remote_active"));
    protected static final PartialModel BUTTON = PartialModel.of(Create.asResource("item/linked_controller/button"));

    static LerpedFloat buttonOffset = LerpedFloat.linear().startWithValue(0);

    static void tick() {
        if (Minecraft.getInstance().isPaused())
            return;

        Player player = Minecraft.getInstance().player;
        buttonOffset.chase(player != null
                && player.getMainHandItem().is(LinkedRemote.ITEM)
                && !player.isCrouching()
                && Minecraft.getInstance().options.keyUse.isDown()
                ? 1 : 0, .4f, LerpedFloat.Chaser.EXP);
        buttonOffset.tickChaser();
    }

    @Override
    protected void render(ItemStack stack, CustomRenderedItemModel model, PartialItemModelRenderer renderer,
                          ItemDisplayContext transformType, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        float pt = AnimationTickHolder.getPartialTicks();

        renderer.render(buttonOffset.getValue(pt) > .5f
                ? POWERED.get() : model.getOriginalModel(), light);
        var msr = TransformStack.of(ms);
        float s = 1 / 16f;

        BakedModel button = BUTTON.get();
        ms.pushPose();
        msr.translate(4*s, 0*s, (1.825-0.45*buttonOffset.getValue(pt))*s);
        msr.scale(1, 1, 0.25f);
        renderer.renderSolid(button, light);
        ms.popPose();

        if (transformType == ItemDisplayContext.GUI)
            return;

        ItemStackHandler inv =  LinkedRemoteItem.getFrequencyItems(stack);
        ms.pushPose();
        ms.translate(0, -.5*s, 0.05*s);
        ms.scale(.22f, .23f, .22f);
        ms.mulPose(Axis.YP.rotationDegrees(180));
        ValueBoxRenderer.renderItemIntoValueBox(inv.getStackInSlot(0), ms, buffer, light, overlay);
        ms.popPose();
        ms.pushPose();
        ms.translate(0, -3*s, 0.05*s);
        ms.scale(.22f, .23f, .22f);
        ms.mulPose(Axis.YP.rotationDegrees(180));
        ValueBoxRenderer.renderItemIntoValueBox(inv.getStackInSlot(1), ms, buffer, light, overlay);
        ms.popPose();
    }
}