package com.tterrag.blur.mixin;

import com.tterrag.blur.Blur;
import com.tterrag.blur.config.BlurConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class MixinInGameHud extends DrawableHelper {
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;
    @Final @Shadow private MinecraftClient client;
    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/util/Identifier;)V", ordinal = 0, shift = At.Shift.BEFORE), method = "render")
    public void blur$onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        if (client.currentScreen == null && client.world != null && Blur.start > 0 && !BlurConfig.blurExclusions.contains(Blur.prevScreen) && Blur.screenHasBackground) {
            fillGradient(matrices, 0, 0, this.scaledWidth, this.scaledHeight, Blur.getBackgroundColor(false, false), Blur.getBackgroundColor(true, false));
        }
    }
}
