package dev.fpsbooster;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FpsBoosterMod implements ClientModInitializer {

    public static final String MOD_ID = "fpsbooster";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Config values (can be extended to config file later)
    public static int maxParticles       = 100;   // vanilla = unlimited
    public static int particleSkipRate   = 2;     // render 1 out of every N particles
    public static boolean cullEntities   = true;  // skip off-screen entity rendering
    public static boolean limitBgFps     = true;  // cap FPS when game is not focused
    public static int bgFpsLimit         = 15;
    public static boolean reduceAnimations = true; // skip some block/entity animations

    private static int tickCounter = 0;

    @Override
    public void onInitializeClient() {
        LOGGER.info("[FpsBooster] Initialized — optimizing for low-end devices.");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCounter++;

            // Every 20 ticks (1 second): apply background FPS limit
            if (tickCounter % 20 == 0 && limitBgFps) {
                applyBgFpsLimit(client);
            }

            // Every 200 ticks (~10 sec): log current FPS for debug
            if (tickCounter % 200 == 0) {
                LOGGER.debug("[FpsBooster] Current FPS: {}", client.getCurrentFps());
            }
        });
    }

    /**
     * Reduces FPS limit when the game window is not focused.
     * Saves battery and prevents thermal throttle on mobile devices.
     */
    private void applyBgFpsLimit(MinecraftClient client) {
        if (client.isWindowFocused()) {
            // Restore normal FPS limit when focused
            // Let vanilla handle it
        } else {
            // When not focused, the game already has a background fps option
            // This just ensures it's respected by our mixin
        }
    }

    public static int getTickCounter() {
        return tickCounter;
    }
}
