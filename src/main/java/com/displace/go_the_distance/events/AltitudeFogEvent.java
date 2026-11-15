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

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        Level level = Minecraft.getInstance().level;
        if (level == null) return;

        double nightStrength = getNightFogStrength(level.getOverworldClockTime(),
                event.getPartialTick());

        // During daylight, leave vanilla fog unchanged.
        if (nightStrength <= 0.0) return;

        Camera camera = event.getCamera();
        double heightAboveStart =
                camera.position().y() - Config.FOG_START_Y_LEVEL.getAsInt();
        double fogAmplifier = Config.FOG_Y_LEVEL_AMPLIFIER.get();

        if (heightAboveStart <= 0.0 || fogAmplifier <= 0.0) {
            return;
        }

        float originalNear = event.getNearPlaneDistance();
        float originalFar = event.getFarPlaneDistance();

        double densityScale = 1.0 + heightAboveStart * fogAmplifier;

        // Never increase the original far distance.
        float fullFogFar =
                (float) Math.clamp(originalFar / densityScale, 8.0, originalFar);

        float fullFogNear = (float) Math.max(0.0, originalNear / densityScale);
        fullFogNear = Math.min(fullFogNear, fullFogFar - 1.0f);

        event.setNearPlaneDistance(lerp(originalNear, fullFogNear, nightStrength));
        event.setFarPlaneDistance(lerp(originalFar, fullFogFar, nightStrength));
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        double nightStrength = getNightFogStrength(level.getOverworldClockTime(),
                event.getPartialTick());

        if (nightStrength <= 0.0) {
            return;
        }

        double heightAboveStart =
                event.getCamera().position().y() - Config.FOG_START_Y_LEVEL.getAsInt();

        if (heightAboveStart <= 0.0 || Config.FOG_Y_LEVEL_AMPLIFIER.get() <= 0.0) {
            return;
        }

        double altitudeStrength = Math.clamp(heightAboveStart / 100.0, 0.0, 1.0);

        double strength = altitudeStrength * nightStrength;

        event.setRed(lerp(event.getRed(), 1.0f, strength));
        event.setGreen(lerp(event.getGreen(), 1.0f, strength));
        event.setBlue(lerp(event.getBlue(), 1.0f, strength));
    }

    private static double getNightFogStrength(long dayTime, double partialTicks) {
        double clockTicks = Math.floorMod(dayTime, 24000L) + 6000.0 + partialTicks;
        double gameHour = (clockTicks % 24000.0) / 1000.0;

        // 19:00–06:00: full altitude fog.
        if (gameHour < 6.0 || gameHour >= 19.0) {
            return 1.0;
        }

        // 06:00–07:00: sunrise dissipates the fog.
        if (gameHour < 7.0) {
            return 1.0 - smoothstep(gameHour - 6.0);
        }

        // 07:00–18:00: no added altitude fog.
        if (gameHour < 18.0) {
            return 0.0;
        }

        // 18:00–19:00: sunset restores the fog.
        return smoothstep(gameHour - 18.0);
    }

    private static double smoothstep(double value) {
        double t = Math.clamp(value, 0.0, 1.0);
        return t * t * (3.0 - 2.0 * t);
    }

    private static float lerp(float start, float end, double strength) {
        return (float) (start + (end - start) * strength);
    }
}