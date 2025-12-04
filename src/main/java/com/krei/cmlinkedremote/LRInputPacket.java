package com.krei.cmlinkedremote;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LRInputPacket {
    private final boolean activated;

    public LRInputPacket(boolean activated) {
        this.activated = activated;
    }

    public LRInputPacket(FriendlyByteBuf buf) {
        this.activated = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(activated);
    }

    public boolean isActivated() {
        return activated;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Handle on server side
            LRServerHandler.handlePacket(this, ctx.get().getSender());
        });
        ctx.get().setPacketHandled(true);
    }
}
