package com.logthedeveloper.tweedledeentweedledum;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;


@EventBusSubscriber(modid = TweedleMod.MODID)
public final class VillagePlushSpawner {
    private static final Queue<PendingChunk> PENDING = new ArrayDeque<>();
    private static final Set<PendingChunk> QUEUED = new HashSet<>();
    private static final Direction[] HORIZONTAL_DIRECTIONS = {
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
    };

    private VillagePlushSpawner() {}

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {

        if (!event.isNewChunk() || !(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        ChunkPos pos = event.getChunk().getPos();
        PendingChunk pending = new PendingChunk(level.dimension(), pos.x, pos.z);
        synchronized (PENDING) {
            if (QUEUED.add(pending)) {
                PENDING.add(pending);
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {

        int attempts;
        synchronized (PENDING) {
            attempts = Math.min(PENDING.size(), 12);
        }

        for (int i = 0; i < attempts; i++) {
            PendingChunk pending;
            synchronized (PENDING) {
                pending = PENDING.poll();
            }
            if (pending == null) {
                break;
            }

            ServerLevel level = event.getServer().getLevel(pending.dimension());
            if (level == null) {
                synchronized (PENDING) { QUEUED.remove(pending); }
                continue;
            }

            LevelChunk chunk = level.getChunkSource().getChunkNow(pending.chunkX(), pending.chunkZ());
            if (chunk == null) {

                synchronized (PENDING) { PENDING.add(pending); }
                continue;
            }

            try {
                addSittingPlushForVillage(level, chunk);
            } finally {
                synchronized (PENDING) { QUEUED.remove(pending); }
            }
        }
    }

    private static void addSittingPlushForVillage(ServerLevel level, LevelChunk chunk) {
        for (Map.Entry<Structure, StructureStart> entry : chunk.getAllStarts().entrySet()) {
            StructureStart start = entry.getValue();
            if (!start.isValid() || !start.getChunkPos().equals(chunk.getPos()) || !isVanillaVillage(level, entry.getKey())) {
                continue;
            }

            if (tryPlaceInVillage(level, chunk, start.getBoundingBox())) {
                return;
            }
        }
    }

    private static boolean isVanillaVillage(ServerLevel level, Structure structure) {

        ResourceLocation id = level.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(structure);
        return id != null && id.getNamespace().equals("minecraft") && id.getPath().startsWith("village_");
    }

    private static boolean tryPlaceInVillage(ServerLevel level, LevelChunk chunk, BoundingBox villageBounds) {
        ChunkPos chunkPos = chunk.getPos();
        int minX = Math.max(villageBounds.minX(), chunkPos.getMinBlockX());
        int maxX = Math.min(villageBounds.maxX(), chunkPos.getMaxBlockX());
        int minZ = Math.max(villageBounds.minZ(), chunkPos.getMinBlockZ());
        int maxZ = Math.min(villageBounds.maxZ(), chunkPos.getMaxBlockZ());
        if (minX > maxX || minZ > maxZ) {
            return false;
        }


        int width = maxX - minX + 1;
        int depth = maxZ - minZ + 1;
        int candidateCount = width * depth;
        int firstCandidate = level.random.nextInt(candidateCount);
        for (int attempt = 0; attempt < candidateCount; attempt++) {
            int index = (firstCandidate + attempt) % candidateCount;
            int x = minX + index % width;
            int z = minZ + index / width;
            BlockPos feet = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z));
            if (!villageBounds.isInside(feet)) {
                continue;
            }

            BlockPos floorPos = feet.below();
            BlockState floor = level.getBlockState(floorPos);
            BlockState feetState = level.getBlockState(feet);
            BlockState headState = level.getBlockState(feet.above());
            if (!floor.isFaceSturdy(level, floorPos, Direction.UP)
                    || !feetState.canBeReplaced()
                    || !headState.canBeReplaced()
                    || !level.getFluidState(feet).isEmpty()
                    || !level.getFluidState(feet.above()).isEmpty()) {
                continue;
            }

            PlushType type = level.random.nextBoolean() ? PlushType.DEE : PlushType.DUMB;
            Direction facing = HORIZONTAL_DIRECTIONS[level.random.nextInt(HORIZONTAL_DIRECTIONS.length)];
            BlockState plushState = ModBlocks.TWEEDLE_DEE.get().defaultBlockState()
                    .setValue(PlushBlock.SITTING, true)
                    .setValue(PlushBlock.FACING, facing);
            if (type == PlushType.DUMB) {
                plushState = ModBlocks.TWEEDLE_DUMB.get().defaultBlockState()
                        .setValue(PlushBlock.SITTING, true)
                        .setValue(PlushBlock.FACING, facing);
            }
            return level.setBlock(feet, plushState, 3);
        }
        return false;
    }

    private record PendingChunk(ResourceKey<Level> dimension, int chunkX, int chunkZ) {}
}
