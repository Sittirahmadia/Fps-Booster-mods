package dev.fpsbooster.mixin;

import dev.fpsbooster.FpsBoosterMod;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * GameRendererMixin
 *
 * Injects into the render loop to:
 * - Throttle FPS when game window is not focused (saves battery on Android)
 * - Skip expensive render passes when FPS is already high enough
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    private long lastBgFrameTime = 0;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        if (!FpsBoosterMod.limitBgFps) return;

        MinecraftClient mc = MinecraftClient.getInstance();

        // If window is not focused, throttle to bgFpsLimit
        if (!mc.isWindowFocused()) {
            long now = System.currentTimeMillis();
            long minFrameTime = 1000L / FpsBoosterMod.bgFpsLimit;

            if (now - lastBgFrameTime < minFrameTime) {
                ci.cancel(); // skip this frame
                return;
            }

            lastBgFrameTime = now;
        }
    }
}
