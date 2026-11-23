
package com.logthedeveloper.tweedledeentweedledum;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Config file: config/tweedledeentweedledum-common.toml
 *
 * Effect entries use the format "effect_id,amplifier".
 * Separate multiple effects with semicolons, for example:
 * "minecraft:speed,0; minecraft:jump_boost,1"
 *
 * Use an empty string ("") to disable effects for that plush.
 */
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue EFFECTS_ENABLED = BUILDER
            .comment("If false plushies on the shoulder give no effects at all.")
            .define("effectsEnabled", true);

    public static final ModConfigSpec.IntValue EFFECT_DURATION_TICKS = BUILDER
            .comment("How long each effect lasts in tick",
                    "Keep this at 220 or more for some effects to reduce flickering",
                    "after the plushie is removed the effects stay for this duration")
            .defineInRange("effectDurationTicks", 220, 25, 72000);

    public static final ModConfigSpec.BooleanValue SHOW_PARTICLES = BUILDER
            .comment("show particles")
            .define("showParticles", false);

    public static final ModConfigSpec.BooleanValue SHOW_ICON = BUILDER
            .comment("snow effect icon")
            .define("showIcon", true);


    public static final ModConfigSpec.ConfigValue<String> DEE_EFFECTS;
    public static final ModConfigSpec.ConfigValue<String> DUMB_EFFECTS;

    public static final ModConfigSpec.DoubleValue SHOULDER_SCALE;
    public static final ModConfigSpec.DoubleValue SHOULDER_OFFSET_X;
    public static final ModConfigSpec.DoubleValue SHOULDER_OFFSET_Y;
    public static final ModConfigSpec.DoubleValue SHOULDER_OFFSET_Z;

    static {
        BUILDER.comment("Tweedle Dee").push("tweedle_dee");

        DEE_EFFECTS = BUILDER
                .comment("Effects while Tweedle Dee is on a shoulder. Use effect_id,amplifier; separate multiple effects with semicolons. Empty text disables effects.")
                .define("effects", "minecraft:speed,0", Config::isValidEffectsString);

        BUILDER.pop();

        BUILDER.comment("Tweedle Dumb").push("tweedle_dumb");

        DUMB_EFFECTS = BUILDER
                .comment("Effects while Tweedle Dumb is on a shoulder. Use effect_id,amplifier; separate multiple effects with semicolons. Empty text disables effects.")
                .define("effects", "minecraft:resistance,0", Config::isValidEffectsString);

        BUILDER.pop();

        BUILDER.comment("How the plushies look on a player's shoulder (client side).")
                .push("shoulder_render");

        SHOULDER_SCALE = BUILDER
                .comment("Size of the plush on the shoulder (1.0 = full block size).")
                .defineInRange("scale", 0.45D, 0.05D, 2.0D);

        SHOULDER_OFFSET_X = BUILDER
                .comment("Move the plush sideways (positive = away from the neck).")
                .defineInRange("offsetX", 0.0D, -2.0D, 2.0D);

        SHOULDER_OFFSET_Y = BUILDER
                .comment("Move the plush up (positive) or down (negative).")
                .defineInRange("offsetY", 0.0D, -2.0D, 2.0D);

        SHOULDER_OFFSET_Z = BUILDER
                .comment("Move the plush forward (positive) or backward (negative).")
                .defineInRange("offsetZ", 0.0D, -2.0D, 2.0D);

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();


    public static List<String> effectsFor(PlushType type) {
        String configured = (type == PlushType.DEE ? DEE_EFFECTS : DUMB_EFFECTS).get();

        if (configured == null || configured.isBlank()) {
            return List.of();
        }

        List<String> entries = new ArrayList<>();

        for (String entry : configured.split(";")) {
            String trimmed = entry.trim();

            if (!trimmed.isEmpty()) {
                entries.add(trimmed);
            }
        }

        return entries;
    }

    private static boolean isValidEffectsString(Object object) {
        if (!(object instanceof String string)) {
            return false;
        }

        if (string.isBlank()) {
            return true;
        }

        String[] entries = string.split(";", -1);

        for (String entry : entries) {
            if (entry.isBlank() || parseEffect(entry) == null) {
                return false;
            }
        }

        return true;
    }


    public static EffectEntry parseEffect(String text) {
        String[] parts = text.trim().split(",");

        if (parts.length < 1 || parts.length > 2) {
            return null;
        }

        ResourceLocation id = ResourceLocation.tryParse(parts[0].trim());

        if (id == null) {
            return null;
        }

        int amplifier = 0;

        if (parts.length == 2) {
            try {
                amplifier = Integer.parseInt(parts[1].trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        if (amplifier < 0 || amplifier > 255) {
            return null;
        }

        return new EffectEntry(id, amplifier);
    }

    public record EffectEntry(ResourceLocation id, int amplifier) {
    }
}
