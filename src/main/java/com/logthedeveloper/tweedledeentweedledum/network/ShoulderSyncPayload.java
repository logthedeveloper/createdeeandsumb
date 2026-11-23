package com.logthedeveloper.tweedledeentweedledum.network;

import com.logthedeveloper.tweedledeentweedledum.ModAttachments;
import com.logthedeveloper.tweedledeentweedledum.PlushType;
import com.logthedeveloper.tweedledeentweedledum.ShoulderData;
import com.logthedeveloper.tweedledeentweedledum.TweedleMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record ShoulderSyncPayload(int entityId, int left, int right) implements CustomPacketPayload {
    public static final Type<ShoulderSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(TweedleMod.MODID, "shoulder_sync"));

    public static final StreamCodec<ByteBuf, ShoulderSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ShoulderSyncPayload::entityId,
            ByteBufCodecs.VAR_INT, ShoulderSyncPayload::left,
            ByteBufCodecs.VAR_INT, ShoulderSyncPayload::right,
            ShoulderSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ShoulderSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Entity entity = context.player().level().getEntity(payload.entityId());
            if (entity instanceof Player player) {
                player.setData(ModAttachments.SHOULDER,
                        new ShoulderData(PlushType.fromNetId(payload.left()), PlushType.fromNetId(payload.right())));
            }
        });
    }
}
