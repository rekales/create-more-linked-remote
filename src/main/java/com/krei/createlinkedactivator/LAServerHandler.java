package com.krei.createlinkedactivator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.simibubi.create.Create;
import com.simibubi.create.content.redstone.link.IRedstoneLinkable;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;

import net.createmod.catnip.data.Couple;
import net.createmod.catnip.data.WorldAttached;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;

public class LAServerHandler implements IPayloadHandler<LAInputPacket> {

    public static WorldAttached<Map<UUID, MobileLinkEntry>> activeActors = new WorldAttached<>($ -> new HashMap<>());

    @SubscribeEvent
    public static void serverTick(ServerTickEvent.Post event) {
        for (LevelAccessor level : event.getServer().getAllLevels()) {
            Map<UUID, MobileLinkEntry> map = activeActors.get(level);
            for (Iterator<Entry<UUID, MobileLinkEntry>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
                Entry<UUID, MobileLinkEntry> entry = iterator.next();
                MobileLinkEntry mle = entry.getValue();
                mle.tickTimeout();
                if (!mle.isAlive()) {
                    // LOGGER.debug("removed " + map.get(entry.getKey()));
                    map.remove(entry.getKey());
                    Create.REDSTONE_LINK_NETWORK_HANDLER.removeFromNetwork(level, mle);
                }
            }
        }
    }

    @SuppressWarnings("null")
    @Override
    public void handle(LAInputPacket packet, IPayloadContext context) {
        Player player = context.player();
        if (!LinkedActivator.ITEM.isIn(player.getMainHandItem()))
            return;
        LevelAccessor level = player.level();
        Map<UUID, MobileLinkEntry> map = activeActors.get(level);
        ItemStack stack = player.getMainHandItem(); // TODO: Check for offhand or add handedness in packet

        if (packet.activated()) {
            MobileLinkEntry entry;
            if (map.containsKey(player.getUUID())) {
                entry = map.get(player.getUUID());
                entry.updatePosition(player.blockPosition());
                entry.resetTimeout();
            } else {
                entry = new MobileLinkEntry(LinkedActivatorItem.getNetworkKey(stack), player.blockPosition());
                map.put(player.getUUID(), entry);
                // LOGGER.debug("added " + map.get(player.getUUID()));
            }
            Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(level, entry);
        } else if (map.containsKey(player.getUUID())) {
            activeActors.get(level).get(player.getUUID()).zeroTimeout();
        }
    }

    static class MobileLinkEntry implements IRedstoneLinkable {
        static final int DEFAULT_TIMEOUT = 10;
        private int timeout;
        private Couple<Frequency> netkey;
        private BlockPos pos;

        public MobileLinkEntry(Couple<Frequency> netkey, BlockPos pos) {
            this.netkey = netkey;
            this.pos = pos;
            this.timeout = DEFAULT_TIMEOUT;
        }

        public void tickTimeout() {
            this.timeout--;
        }

        public void resetTimeout() {
            this.timeout = DEFAULT_TIMEOUT;
        }

        public void zeroTimeout() { // Needs a better name
            timeout = 0;
        }

        public void updatePosition(BlockPos pos) {
            this.pos = pos;
        }

        @Override
        public int getTransmittedStrength() {
            return isAlive() ? 15 : 0;
        }

        @Override
        public void setReceivedStrength(int power) {
        }

        @Override
        public boolean isListening() {
            return false;
        }

        @Override
        public boolean isAlive() {
            return timeout > 0;
        }

        @Override
        public Couple<Frequency> getNetworkKey() {
            return netkey;
        }

        @Override
        public BlockPos getLocation() {
            return pos;
        }

        @Override
        public String toString() {
            return this.netkey.getFirst().getStack().getItem().toString() + " "
                    + this.netkey.getSecond().getStack().getItem().toString();
        }
    }
}
