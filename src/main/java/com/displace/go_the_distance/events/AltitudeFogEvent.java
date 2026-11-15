package com.displace.go_the_distance.events;

import com.displace.go_the_distance.Config;
import com.displace.go_the_distance.GoTheDistance;
import net.minecraft.client.Camera;
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
        Camera camera = event.getCamera();
        double yLevel = camera.position().y();
        int startingYLevel = Config.FOG_START_Y_LEVEL.getAsInt();
        double fogAmplifier = Config.FOG_Y_LEVEL_AMPLIFIER.get();
        double currentFogAmount = 0.0f;

        if (yLevel < startingYLevel) {
            return;
        }

        event.setNearPlaneDistance(10);
        event.setFarPlaneDistance(100);
    }

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        Camera camera = event.getCamera();
        double yLevel = camera.position().y();
        int startingYLevel = Config.FOG_START_Y_LEVEL.getAsInt();
        if (yLevel < startingYLevel) {
            return;
        }
        event.setRed(256);
        event.setGreen(256);
        event.setBlue(256);
    }
}
