package com.logthedeveloper.tweedledeentweedledum.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(ShoulderSyncPayload.TYPE, ShoulderSyncPayload.STREAM_CODEC, ShoulderSyncPayload::handle);
        registrar.playToServer(TakeOffPayload.TYPE, TakeOffPayload.STREAM_CODEC, TakeOffPayload::handle);
    }
}
