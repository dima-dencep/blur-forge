package com.tterrag.blur.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.tterrag.blur.Blur;

import net.minecraft.client.gui.screen.Screen;

@Mixin(Screen.class)
public abstract class MixinScreen {
    @ModifyConstant(
            method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            constant = @Constant(
                    intValue = -1072689136
            ),
            require = 0
    )
    private int blur$getFirstBackgroundColor(int color) {
        return Blur.getBackgroundColor(false, true);
    }

    @ModifyConstant(
            method = "renderBackground(Lnet/minecraft/client/util/math/MatrixStack;I)V",
            constant = @Constant(
                    intValue = -804253680
            ),
            require = 0
    )
    private int blur$getSecondBackgroundColor(int color) {
        return Blur.getBackgroundColor(true, true);
    }
}
