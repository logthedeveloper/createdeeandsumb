package com.logthedeveloper.tweedledeentweedledum;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public record ShoulderData(PlushType left, PlushType right, CompoundTag leftItemData, CompoundTag rightItemData) {
    public static final ShoulderData EMPTY = new ShoulderData(null, null, null, null);

    public static final Codec<ShoulderData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlushType.CODEC.optionalFieldOf("left").forGetter(data -> Optional.ofNullable(data.left())),
            PlushType.CODEC.optionalFieldOf("right").forGetter(data -> Optional.ofNullable(data.right())),
            CompoundTag.CODEC.optionalFieldOf("left_item_data").forGetter(data -> Optional.ofNullable(data.leftItemData())),
            CompoundTag.CODEC.optionalFieldOf("right_item_data").forGetter(data -> Optional.ofNullable(data.rightItemData()))
    ).apply(instance, (left, right, leftData, rightData) ->
            new ShoulderData(left.orElse(null), right.orElse(null), leftData.orElse(null), rightData.orElse(null))));

    public ShoulderData(PlushType left, PlushType right) {
        this(left, right, null, null);
    }

    public ShoulderData {
        leftItemData = leftItemData == null ? null : leftItemData.copy();
        rightItemData = rightItemData == null ? null : rightItemData.copy();
    }

    public boolean isEmpty() {
        return left == null && right == null;
    }

    public boolean hasFreeSlot() {
        return left == null || right == null;
    }

    public ShoulderData withLeft(PlushType type, CompoundTag itemData) {
        return new ShoulderData(type, right, type == null ? null : itemData, rightItemData);
    }

    public ShoulderData withRight(PlushType type, CompoundTag itemData) {
        return new ShoulderData(left, type, leftItemData, type == null ? null : itemData);
    }

    public ShoulderData withLeft(PlushType type) {
        return withLeft(type, null);
    }

    public ShoulderData withRight(PlushType type) {
        return withRight(type, null);
    }


    public List<PlushType> worn() {
        List<PlushType> list = new ArrayList<>(2);
        if (left != null) list.add(left);
        if (right != null) list.add(right);
        return list;
    }
}
