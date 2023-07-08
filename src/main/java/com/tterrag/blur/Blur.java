package com.tterrag.blur;

import com.tterrag.blur.config.BlurConfig;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.Uniform1f;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.NetworkConstants;

@Mod(Blur.MODID)
public class Blur {
    public static final BlurConfig blurConfig = AutoConfig.register(BlurConfig.class, GsonConfigSerializer::new).get();
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final String MODID = "blur";
    public static long start;

    private static final ManagedShaderEffect blur = ShaderEffectManager.getInstance().manage(new Identifier(MODID, "shaders/post/fade_in_blur.json"),
            shader -> shader.setUniformValue("Radius", blurConfig.radius));
    private static final Uniform1f blurProgress = blur.findUniform1f("Progress");

    public Blur() {
        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> NetworkConstants.IGNORESERVERONLY,
                        (a, b) -> true
                )
        );

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, screen) -> AutoConfig.getConfigScreen(BlurConfig.class, screen).get()
                )
        );

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onShaderEffectRender(ShaderEffectRenderCallback event) {
        if (start > 0) {
            blurProgress.set(getProgress());
            blur.render(event.tickDelta);
        }
    }

    private static boolean doFade = false;

    public static void onScreenChange(Screen newGui) {
        if (client.world != null) {
            boolean excluded = newGui == null || blurConfig.blurExclusions.stream().anyMatch(exclusion -> newGui.getClass().getName().contains(exclusion));
            if (!excluded) {
                if (blurConfig.showScreenTitle) System.out.println(newGui.getClass().getName());

                blur.setUniformValue("Radius", blurConfig.radius);
                if (doFade) {
                    start = System.currentTimeMillis();
                    doFade = false;
                }
            } else {
                start = -1;
                doFade = true;
            }
        }
    }

    private static float getProgress() {
        float x = Math.min((System.currentTimeMillis() - start) / (float) blurConfig.fadeTimeMillis, 1);
        if (blurConfig.ease) x *= (2 - x);  // easeInCubic
        return x;
    }

    public static int getBackgroundColor(boolean second) {
        int col = second ? blurConfig.gradientEnd : blurConfig.gradientStart;
        int a = col >>> 24;
        int r = (col >> 16) & 0xFF;
        int b = (col >> 8) & 0xFF;
        int g = col & 0xFF;
        float prog = getProgress();
        a *= prog;
        r *= prog;
        g *= prog;
        b *= prog;
        return a << 24 | r << 16 | b << 8 | g;
    }
}
