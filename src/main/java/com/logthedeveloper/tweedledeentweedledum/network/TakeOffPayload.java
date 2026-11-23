package com.logthedeveloper.tweedledeentweedledum.network;

import com.logthedeveloper.tweedledeentweedledum.ShoulderManager;
import com.logthedeveloper.tweedledeentweedledum.TweedleMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record TakeOffPayload() implements CustomPacketPayload {
    public static final Type<TakeOffPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TweedleMod.MODID, "take_off"));

    public static final StreamCodec<ByteBuf, TakeOffPayload> STREAM_CODEC = StreamCodec.unit(new TakeOffPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TakeOffPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ShoulderManager.giveBack(player);
            }
        });
    }
}
