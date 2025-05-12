package com.krei.createlinkedactivator;

import static com.krei.createlinkedactivator.LinkedActivator.*;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record LAInputPacket(boolean activated) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<LAInputPacket> TYPE = 
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MODID, "activated_packet"));

    public static final StreamCodec<ByteBuf, LAInputPacket> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, LAInputPacket::activated, LAInputPacket::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
}
