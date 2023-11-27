package com.tterrag.blur.mixin;

import com.tterrag.blur.Blur;
import com.tterrag.blur.config.BlurConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public abstract class MixinInGameHud extends InGameHud {
    public MixinInGameHud(MinecraftClient client, ItemRenderer itemRenderer) {
        super(client, itemRenderer);
    }

    @Inject(at = @At("TAIL"), method = "render")
    public void blur$onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (client.currentScreen == null && client.world != null && Blur.start > 0 && !BlurConfig.INSTANCE.blurExclusions.get().contains(Blur.prevScreen) && Blur.screenHasBackground) {
            context.fillGradient(0, 0, this.scaledWidth, this.scaledHeight, Blur.getBackgroundColor(false, false), Blur.getBackgroundColor(true, false));
        }
    }
}
