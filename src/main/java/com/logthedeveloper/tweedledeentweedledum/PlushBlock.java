package com.logthedeveloper.tweedledeentweedledum;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class PlushBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final BooleanProperty SITTING = BooleanProperty.create("sitting");
    public static final MapCodec<PlushBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            propertiesCodec(),
            PlushType.CODEC.fieldOf("plush").forGetter(PlushBlock::getPlushType)
    ).apply(instance, (properties, type) -> new PlushBlock(type, properties)));


    private static final VoxelShape SHAPE_NORTH_SOUTH = Block.box(2.5D, 0.0D, 4.0D, 13.5D, 15.5D, 12.0D);
    private static final VoxelShape SHAPE_EAST_WEST = Block.box(4.0D, 0.0D, 2.5D, 12.0D, 15.5D, 13.5D);

    private final PlushType plushType;

    public PlushBlock(PlushType plushType, Properties properties) {
        super(properties);
        this.plushType = plushType;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SITTING, false));
    }

    public PlushType getPlushType() {
        return plushType;
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SITTING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(SITTING, false);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? SHAPE_EAST_WEST : SHAPE_NORTH_SOUTH;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PlushBlockEntity(pos, state);
    }


    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {

        if (!player.isShiftKeyDown()) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.cycle(SITTING), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!ShoulderManager.hasFreeSlot(player)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message." + TweedleMod.MODID + ".shoulders_full"), true);
            }
            return InteractionResult.FAIL;
        }
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            CompoundTag itemData = level.getBlockEntity(pos) instanceof PlushBlockEntity tankEntity
                    ? tankEntity.writeItemData(level.registryAccess()) : new CompoundTag();
            if (ShoulderManager.mount(serverPlayer, plushType, itemData)) {
                level.removeBlock(pos, false);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
