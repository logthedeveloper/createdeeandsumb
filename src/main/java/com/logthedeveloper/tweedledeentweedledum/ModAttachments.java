package com.logthedeveloper.tweedledeentweedledum;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, TweedleMod.MODID);


    public static final Supplier<AttachmentType<ShoulderData>> SHOULDER = ATTACHMENT_TYPES.register("shoulder",
            () -> AttachmentType.builder(() -> ShoulderData.EMPTY).serialize(ShoulderData.CODEC).build());
}
