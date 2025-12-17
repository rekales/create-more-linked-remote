package com.kreidev.cmlinkedremote;

import static com.kreidev.cmlinkedremote.LinkedRemote.*;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record LRInputPacket(boolean activated) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LRInputPacket> TYPE =
            new CustomPacketPayload.Type<>(resLoc("activated_packet"));

    public static final StreamCodec<ByteBuf, LRInputPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, LRInputPacket::activated, LRInputPacket::new);

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
}
