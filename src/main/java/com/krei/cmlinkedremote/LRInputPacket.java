package com.krei.cmlinkedremote;

import static com.krei.cmlinkedremote.LinkedRemote.*;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record LRInputPacket(boolean activated) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LRInputPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "activated_packet"));

    public static final StreamCodec<ByteBuf, LRInputPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, LRInputPacket::activated, LRInputPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
}
