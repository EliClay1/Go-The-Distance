package com.displace.go_the_distance;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue FOG_START_Y_LEVEL = BUILDER
            .comment("The y-level where altitude fog begins to take effect.")
            .defineInRange("fogStartYLevel", 120, -64, 319);

    public static final ModConfigSpec.ConfigValue<Double> FOG_Y_LEVEL_AMPLIFIER = BUILDER
            .comment("The amount of amplification in fog as you increment per each y-level above the fog start level.")
            .defineInRange("fogYLevelAmplifier", 0.05, 0.0, 1.0);

    static final ModConfigSpec SPEC = BUILDER.build();
}
