package com.logthedeveloper.tweedledeentweedledum;

import com.logthedeveloper.tweedledeentweedledum.network.ShoulderSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.network.PacketDistributor;

/** All the logic for putting plushies on / taking them off the shoulders. Server side except hasFreeSlot. */
public final class ShoulderManager {
    private ShoulderManager() {
    }


    public static boolean hasFreeSlot(Player player) {
        return player.getData(ModAttachments.SHOULDER).hasFreeSlot();
    }


    public static boolean mount(ServerPlayer player, PlushType type) {
        return mount(player, type, null);
    }


    public static boolean mount(ServerPlayer player, PlushType type, CompoundTag itemData) {
        ShoulderData data = player.getData(ModAttachments.SHOULDER);
        ShoulderData updated;
        if (data.right() == null) {
            updated = data.withRight(type, itemData);
        } else if (data.left() == null) {
            updated = data.withLeft(type, itemData);
        } else {
            return false;
        }
        player.setData(ModAttachments.SHOULDER, updated);
        sync(player);
        player.level().playSound(null, player.blockPosition(), SoundEvents.WOOL_PLACE, SoundSource.PLAYERS, 0.8F, 1.2F);
        return true;
    }


    public static ItemStack dismount(ServerPlayer player) {
        ShoulderData data = player.getData(ModAttachments.SHOULDER);
        ItemStack removed;
        ShoulderData updated;
        if (data.left() != null) {
            removed = PlushItem.createWithBlockEntityData(data.left(), data.leftItemData());
            updated = data.withLeft(null, null);
        } else if (data.right() != null) {
            removed = PlushItem.createWithBlockEntityData(data.right(), data.rightItemData());
            updated = data.withRight(null, null);
        } else {
            return ItemStack.EMPTY;
        }
        player.setData(ModAttachments.SHOULDER, updated);
        sync(player);
        player.level().playSound(null, player.blockPosition(), SoundEvents.WOOL_PLACE, SoundSource.PLAYERS, 0.8F, 0.9F);
        return removed;
    }


    public static void giveBack(ServerPlayer player) {
        ItemStack stack = dismount(player);
        if (!stack.isEmpty() && !player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }


    public static void sync(ServerPlayer player) {
        PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, payloadFor(player));
    }


    public static void syncTo(ServerPlayer viewer, ServerPlayer target) {
        PacketDistributor.sendToPlayer(viewer, payloadFor(target));
    }

    private static ShoulderSyncPayload payloadFor(ServerPlayer player) {
        ShoulderData data = player.getData(ModAttachments.SHOULDER);
        return new ShoulderSyncPayload(player.getId(), PlushType.toNetId(data.left()), PlushType.toNetId(data.right()));
    }
}
