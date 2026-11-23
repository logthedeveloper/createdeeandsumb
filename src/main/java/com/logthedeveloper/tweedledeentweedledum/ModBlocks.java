package com.logthedeveloper.tweedledeentweedledum;

import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TweedleMod.MODID);

    public static final DeferredBlock<PlushBlock> TWEEDLE_DEE = BLOCKS.register("tweedle_dee",
            () -> new PlushBlock(PlushType.DEE, plushProperties(MapColor.COLOR_LIGHT_BLUE)));

    public static final DeferredBlock<PlushBlock> TWEEDLE_DUMB = BLOCKS.register("tweedle_dumb",
            () -> new PlushBlock(PlushType.DUMB, plushProperties(MapColor.COLOR_RED)));

    private static BlockBehaviour.Properties plushProperties(MapColor color) {
        return BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(0.4F)
                .sound(SoundType.WOOL)
                .noOcclusion();
    }
}
