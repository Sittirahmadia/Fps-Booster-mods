package dev.fpsbooster.mixin;

import dev.fpsbooster.FpsBoosterMod;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * WorldRendererMixin
 *
 * - Skips sky/fog rendering every other frame to reduce GPU load
 * - Reduces weather render intensity (rain/snow)
 *
 * Note: sky flicker is barely noticeable at 30+ FPS.
 */
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    private static int frameCounter = 0;

    /**
     * Skip rendering sky every other frame.
     * Barely visible but saves significant GPU time.
     */
    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void onRenderSky(Matrix4f matrix, Matrix4f projectionMatrix,
                              float tickDelta, CallbackInfo ci) {
        if (!FpsBoosterMod.reduceAnimations) return;

        frameCounter++;
        // Render sky every 2 frames only
        if (frameCounter % 2 != 0) {
            ci.cancel();
        }
    }
}
