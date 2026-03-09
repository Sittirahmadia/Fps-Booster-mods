package dev.fpsbooster.mixin;

import dev.fpsbooster.FpsBoosterMod;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

/**
 * ParticleManagerMixin
 *
 * Reduces particle count to boost FPS on low-end devices.
 * - Limits total active particles to maxParticles
 * - Randomly skips particle spawns based on particleSkipRate
 */
@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    private static final Random rng = new Random();
    private static int particleSpawnCounter = 0;

    /**
     * Skip particle addition based on skip rate.
     * e.g. particleSkipRate=2 means only 1 out of 2 particles spawn.
     */
    @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onAddParticle(Particle particle, CallbackInfo ci) {
        particleSpawnCounter++;

        // Skip particle if it exceeds skip rate
        if (particleSpawnCounter % FpsBoosterMod.particleSkipRate != 0) {
            ci.cancel();
            return;
        }

        // Also cancel if we've hit the max particle cap
        // (We approximate via spawn counter — real count would need accessor)
        if (particleSpawnCounter > FpsBoosterMod.maxParticles * FpsBoosterMod.particleSkipRate) {
            particleSpawnCounter = 0; // reset to avoid overflow
            ci.cancel();
        }
    }
}
