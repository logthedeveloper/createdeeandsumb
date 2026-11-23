package com.logthedeveloper.tweedledeentweedledum;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;

/** A persistent 100,000 mB tank attached to each placed Tweedle plushie. */
public class PlushBlockEntity extends BlockEntity {
    public static final int TANK_CAPACITY = 100_000_000; // 100 buckets (mB)
    private final FluidTank fluidTank;

    public PlushBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PLUSH.get(), pos, state);
        this.fluidTank = new FluidTank(TANK_CAPACITY) {
            @Override
            protected void onContentsChanged() {
                PlushBlockEntity.this.setChanged();
                if (PlushBlockEntity.this.level != null && !PlushBlockEntity.this.level.isClientSide) {
                    PlushBlockEntity.this.level.sendBlockUpdated(
                            PlushBlockEntity.this.worldPosition,
                            PlushBlockEntity.this.getBlockState(),
                            PlushBlockEntity.this.getBlockState(),
                            3
                    );
                }
            }
        };
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    /** Data placed on the plush item so a filled plush stays filled when picked up. */
    public CompoundTag writeItemData() {
        return level == null ? new CompoundTag() : writeItemData(level.registryAccess());
    }

    /** Data placed on the plush item, using the world's registry provider explicitly. */
    public CompoundTag writeItemData(HolderLookup.Provider registries) {
        CompoundTag itemData = new CompoundTag();
        if (!fluidTank.isEmpty()) {
            itemData.put("FluidTank", fluidTank.writeToNBT(registries, new CompoundTag()));
        }
        return itemData;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("FluidTank", fluidTank.writeToNBT(registries, new CompoundTag()));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("FluidTank", CompoundTag.TAG_COMPOUND)) {
            fluidTank.readFromNBT(registries, tag.getCompound("FluidTank"));
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
