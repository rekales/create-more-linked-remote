package com.krei.createlinkedactivator;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
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

}