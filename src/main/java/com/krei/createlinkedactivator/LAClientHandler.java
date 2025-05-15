package com.krei.createlinkedactivator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class LAClientHandler {

    @SubscribeEvent
    public static void clientTick(ClientTickEvent.Pre event) {
        Player player = Minecraft.getInstance().player;
        if (player != null
                && player.getMainHandItem().is(LinkedActivator.ITEM)
                && !Minecraft.getInstance().isPaused()) {
            if (!player.isCrouching()) {
                PacketDistributor.sendToServer(new LAInputPacket(Minecraft.getInstance().options.keyUse.isDown()));  // TODO: Check for offhand without overriding
            }
        }
    }

    public static void clientSetup(FMLClientSetupEvent event) {

        ItemProperties.register(LinkedActivator.ITEM.get(), ResourceLocation.fromNamespaceAndPath(LinkedActivator.MODID, "active"),
                (itemstack, level, entity, seed) -> entity != null
                        && entity.getMainHandItem().equals(itemstack)
                        && !Minecraft.getInstance().isPaused()
                        && !entity.isCrouching()
                        && Minecraft.getInstance().options.keyUse.isDown() ? 1.0f : 0.0f);
    }
}