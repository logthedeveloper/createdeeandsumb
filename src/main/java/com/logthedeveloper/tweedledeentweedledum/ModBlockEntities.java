package com.logthedeveloper.tweedledeentweedledum;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TweedleMod.MODID);

    public static final Supplier<BlockEntityType<PlushBlockEntity>> PLUSH = BLOCK_ENTITY_TYPES.register("plush",
            () -> BlockEntityType.Builder.of(PlushBlockEntity::new,
                    ModBlocks.TWEEDLE_DEE.get(), ModBlocks.TWEEDLE_DUMB.get()).build(null));

    private ModBlockEntities() {}


    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, PLUSH.get(),
                (blockEntity, side) -> blockEntity.getFluidTank());
    }
}
