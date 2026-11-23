package com.logthedeveloper.tweedledeentweedledum;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TweedleMod.MODID);

    public static final DeferredItem<PlushItem> TWEEDLE_DEE = ITEMS.register("tweedle_dee",
            () -> new PlushItem(ModBlocks.TWEEDLE_DEE.get(), new Item.Properties().stacksTo(16), PlushType.DEE));

    public static final DeferredItem<PlushItem> TWEEDLE_DUMB = ITEMS.register("tweedle_dumb",
            () -> new PlushItem(ModBlocks.TWEEDLE_DUMB.get(), new Item.Properties().stacksTo(16), PlushType.DUMB));


    public static final DeferredItem<Item> DEE_FABRIC = ITEMS.register("dee_fabric",
            () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DUMB_FABRIC = ITEMS.register("dumb_fabric",
            () -> new Item(new Item.Properties()));

    public static ItemLike itemFor(PlushType type) {
        return type == PlushType.DEE ? TWEEDLE_DEE.get() : TWEEDLE_DUMB.get();
    }
}
