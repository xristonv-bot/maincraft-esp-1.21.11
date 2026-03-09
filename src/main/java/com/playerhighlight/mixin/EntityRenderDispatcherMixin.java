package com.playerhighlight.mixin;

import com.playerhighlight.PlayerHighlightClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {

    @Inject(method = "getOutlineColor", at = @At("HEAD"), cancellable = true)
    private void overridePlayerOutlineColor(Entity entity, CallbackInfoReturnable<Integer> cir) {
        if (!PlayerHighlightClient.highlightEnabled) return;

        Minecraft client = Minecraft.getInstance();
        boolean isSelf = (entity == client.player);

        if (entity instanceof Player && (!isSelf || PlayerHighlightClient.config.highlightSelf)) {
            if (PlayerHighlightClient.config.pulseEffect) {
                float pulse = (float)(Math.sin(System.currentTimeMillis() / 600.0) * 0.5 + 0.5);
                int r = (int)(80 + 175 * pulse);
                int g = (int)(20 + 50 * (1f - pulse));
                int b = 255;
                cir.setReturnValue((0xFF << 24) | (r << 16) | (g << 8) | b);
            } else {
                var cfg = PlayerHighlightClient.config;
                cir.setReturnValue((0xFF << 24) | (cfg.colorR << 16) | (cfg.colorG << 8) | cfg.colorB);
            }
        }
    }
}
