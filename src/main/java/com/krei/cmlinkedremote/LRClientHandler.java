package com.krei.cmlinkedremote;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=LinkedRemote.MODID, value= Dist.CLIENT)
public class LRClientHandler {

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            LinkedRemoteItemRenderer.tick();
            Player player = Minecraft.getInstance().player;
            if (player != null
                    && player.getMainHandItem().is(LinkedRemote.ITEM)
                    && !Minecraft.getInstance().isPaused()) {
                if (!player.isCrouching()) {
                    LinkedRemote.INSTANCE.sendToServer(new LRInputPacket(Minecraft.getInstance().options.keyUse.isDown()));  // TODO: Check for offhand without overriding
                }
            }
        }
    }
}