package dev.fpsbooster.mixin;

import dev.fpsbooster.FpsBoosterMod;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * EntityRendererMixin
 *
 * Skips rendering entities that are:
 * - Behind the player (basic back-face culling)
 * - Too far from the camera beyond render distance
 *
 * This is a simplified frustum cull — Sodium does this more thoroughly,
 * but this helps even without Sodium installed.
 */
@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

    private static final double MAX_ENTITY_RENDER_DIST_SQ = 64 * 64; // 64 blocks squared

    @Inject(
        method = "render",
        at = @At("HEAD"),
        cancellable = true
    )
    private void onRender(T entity, float yaw, float tickDelta,
                          MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                          int light, CallbackInfo ci) {

        if (!FpsBoosterMod.cullEntities) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        // Skip rendering entities far beyond normal view
        double distSq = mc.player.squaredDistanceTo(entity);
        if (distSq > MAX_ENTITY_RENDER_DIST_SQ * 2) {
            ci.cancel();
            return;
        }

        // Skip rendering entities directly behind the player
        // (dot product of look vector and entity direction)
        Vec3d playerLook = mc.player.getRotationVector();
        Vec3d toEntity   = entity.getPos().subtract(mc.player.getPos()).normalize();

        double dot = playerLook.dotProduct(toEntity);
        // If entity is more than ~120° behind (dot < -0.5) and far enough, skip
        if (dot < -0.5 && distSq > 16 * 16) {
            ci.cancel();
        }
    }
}
