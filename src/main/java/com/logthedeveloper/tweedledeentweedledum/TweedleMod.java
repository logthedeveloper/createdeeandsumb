package com.logthedeveloper.tweedledeentweedledum;

import com.logthedeveloper.tweedledeentweedledum.network.ModNetwork;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;


@Mod(TweedleMod.MODID)
public class TweedleMod {
    public static final String MODID = "tweedledeentweedledum";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TweedleMod(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(ModBlockEntities::registerCapabilities);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);

        modEventBus.addListener(ModNetwork::register);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
