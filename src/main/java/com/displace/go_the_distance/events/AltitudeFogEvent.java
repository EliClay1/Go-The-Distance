package com.displace.go_the_distance.events;

import com.displace.go_the_distance.Config;
import com.displace.go_the_distance.GoTheDistance;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(modid = GoTheDistance.MODID, value = Dist.CLIENT)
public final class AltitudeFogEvent {
    private AltitudeFogEvent() {
    }

    // mutable global variables?


    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Minecraft minecraft = Minecraft.getInstance();
        Camera camera = event.getCamera();

        Level level = minecraft.level;
        if (level == null) return;

        int gameHour = getMinecraftHour(level.getOverworldClockTime());

        double yLevel = camera.position().y();
        int startingYLevel = Config.FOG_START_Y_LEVEL.getAsInt();
        double fogAmplifier = Config.FOG_Y_LEVEL_AMPLIFIER.get();

        double heightAboveStart = yLevel - startingYLevel;

        if (heightAboveStart <= 0.0 || fogAmplifier <= 0.0) return;

        float originalNear = event.getNearPlaneDistance();
        float originalFar = event.getFarPlaneDistance();
        double densityScale = 1.0 + heightAboveStart * fogAmplifier;

        float farDistance = (float) Math.max(8.0, originalFar / densityScale);
        float nearDistance = (float) Math.max(0.0, originalNear / densityScale);

        // Keep the fog's starting distance below its ending distance.
        nearDistance = Math.min(nearDistance, farDistance - 1.0f);

        event.setNearPlaneDistance(nearDistance);
        event.setFarPlaneDistance(farDistance);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        double yLevel = event.getCamera().position().y();
        int startingYLevel = Config.FOG_START_Y_LEVEL.getAsInt();

        if (yLevel <= startingYLevel) return;

        // Example: transition fully to white across 100 vertical blocks.
        float strength = (float) Math.clamp((yLevel - startingYLevel) / 100.0, 0.0, 1.0);

        event.setRed(event.getRed() + (1.0f - event.getRed()) * strength);
        event.setGreen(event.getGreen() + (1.0f - event.getGreen()) * strength);
        event.setBlue(event.getBlue() + (1.0f - event.getBlue()) * strength);
    }

    public static int getMinecraftHour(long dayTime) {
        return (int) (Math.floorMod(dayTime + 6000, 24000) / 1000);
    }
}
