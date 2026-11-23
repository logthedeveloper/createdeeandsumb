package com.logthedeveloper.tweedledeentweedledum;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TweedleMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + TweedleMod.MODID))
                    .icon(() -> new ItemStack(ModItems.TWEEDLE_DEE.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.TWEEDLE_DEE.get());
                        output.accept(ModItems.TWEEDLE_DUMB.get());
                        output.accept(ModItems.DEE_FABRIC.get());
                        output.accept(ModItems.DUMB_FABRIC.get());
                    })
                    .build());
}
