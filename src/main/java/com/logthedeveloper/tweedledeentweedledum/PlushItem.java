package com.logthedeveloper.tweedledeentweedledum;

import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.Locale;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;


public class PlushItem extends BlockItem {
    private final PlushType plushType;

    public PlushItem(Block block, Properties properties, PlushType plushType) {
        super(block, properties);
        this.plushType = plushType;
    }

    public PlushType getPlushType() {
        return plushType;
    }


    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        CompoundTag blockEntityData = getBlockEntityData(stack);
        if (!blockEntityData.contains("FluidTank", CompoundTag.TAG_COMPOUND) || context.registries() == null) {
            return;
        }

        FluidTank previewTank = new FluidTank(PlushBlockEntity.TANK_CAPACITY);
        previewTank.readFromNBT(context.registries(), blockEntityData.getCompound("FluidTank"));
        if (previewTank.isEmpty()) {
            return;
        }

        tooltip.add(Component.translatable("tooltip." + TweedleMod.MODID + ".stored_fluid",
                previewTank.getFluid().getHoverName()).withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("tooltip." + TweedleMod.MODID + ".fluid_amount",
                String.format(Locale.ROOT, "%,d", previewTank.getFluidAmount()),
                String.format(Locale.ROOT, "%,d", PlushBlockEntity.TANK_CAPACITY))
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!ShoulderManager.hasFreeSlot(player)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message." + TweedleMod.MODID + ".shoulders_full"), true);
            }
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            if (ShoulderManager.mount(serverPlayer, plushType, getBlockEntityData(stack))) {
                stack.consume(1, player);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }


    public static ItemStack createWithBlockEntityData(PlushType type, CompoundTag blockEntityData) {
        ItemStack stack = new ItemStack(ModItems.itemFor(type));
        if (blockEntityData != null && !blockEntityData.isEmpty()) {
            // Use the vanilla helper so the component carries the correct block-entity type id.
            BlockItem.setBlockEntityData(stack, ModBlockEntities.PLUSH.get(), blockEntityData.copy());
        }
        return stack;
    }


    public static CompoundTag getBlockEntityData(ItemStack stack) {
        net.minecraft.world.item.component.CustomData customData =
                stack.get(net.minecraft.core.component.DataComponents.BLOCK_ENTITY_DATA);
        return customData == null ? new CompoundTag() : customData.copyTag();
    }
}
