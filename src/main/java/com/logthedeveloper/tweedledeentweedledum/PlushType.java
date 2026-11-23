package com.logthedeveloper.tweedledeentweedledum;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;


public enum PlushType implements StringRepresentable {
    DEE("dee"),
    DUMB("dumb");

    public static final Codec<PlushType> CODEC = StringRepresentable.fromEnum(PlushType::values);

    private final String serializedName;

    PlushType(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }


    public static int toNetId(PlushType type) {
        return type == null ? 0 : type.ordinal() + 1;
    }

    public static PlushType fromNetId(int id) {
        PlushType[] values = values();
        return id >= 1 && id <= values.length ? values[id - 1] : null;
    }
}
