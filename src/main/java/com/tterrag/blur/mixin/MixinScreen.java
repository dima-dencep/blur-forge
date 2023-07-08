package com.tterrag.blur.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.tterrag.blur.Blur;

import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class MixinScreen {

    @Shadow @Nullable protected MinecraftClient client;

    @Shadow @Final protected Text title;
    private final Text blurConfig = Text.translatable("blur.midnightconfig.title");

    @Inject(at = @At("HEAD"), method = "tick")
    private void blur$reloadShader(CallbackInfo ci) {
        if (this.client != null && this.title.equals(blurConfig)) {
            Blur.onScreenChange(this.client.currentScreen);
        }
    }
    @Inject(at = @At("HEAD"), method = "renderBackground")
    public void blur$getBackgroundEnabled(MatrixStack matrices, CallbackInfo ci) {
        if (this.client != null && this.client.world != null) {
            Blur.screenHasBackground = true;
        }
    }

    @ModifyConstant(
            method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            constant = @Constant(intValue = -1072689136))
    private int blur$getFirstBackgroundColor(int color) {
        return Blur.getBackgroundColor(false, true);
    }

    @ModifyConstant(
            method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            constant = @Constant(intValue = -804253680))
    private int blur$getSecondBackgroundColor(int color) {
        return Blur.getBackgroundColor(true, true);
    }
}
