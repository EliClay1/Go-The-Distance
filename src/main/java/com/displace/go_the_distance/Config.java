package com.displace.go_the_distance;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue FOG_START_Y_LEVEL = BUILDER
            .comment("The y-level where altitude fog begins to take effect.")
            .defineInRange("fogStartYLevel", 100, -64, 319);

    public static final ModConfigSpec.ConfigValue<Double> FOG_Y_LEVEL_AMPLIFIER = BUILDER
            .comment("The amount of amplification in fog as you increment per each y-level above the fog start level.")
            .defineInRange("fogYLevelAmplifier", 0.05, 0.0, 1.0);

    static final ModConfigSpec SPEC = BUILDER.build();
}
