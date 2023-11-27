package com.tterrag.blur;

import com.tterrag.blur.config.BlurConfig;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.Uniform1f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import java.awt.Color;
import java.util.Objects;

@Mod(Blur.MODID)
public class Blur {
    public static final String MODID = "blur";
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static long start;
    public static String prevScreen;
    public static String oldScreen;
    public static boolean screenHasBackground;

    private static final ManagedShaderEffect blur = ShaderEffectManager.getInstance().manage(new Identifier(MODID, "shaders/post/fade_in_blur.json"),
            shader -> shader.setUniformValue("Radius", BlurConfig.INSTANCE.radius.get().floatValue()));
    private static final Uniform1f blurProgress = blur.findUniform1f("Progress");

    public Blur() {
        BlurConfig.CONFIG_SPEC_PAIR.getValue(); // pseudo-register

        MinecraftForge.EVENT_BUS.<ShaderEffectRenderCallback>addListener(event -> {
            if (start > 0) {
                blurProgress.set(getProgress(client.currentScreen != null));
                blur.render(event.tickDelta);
            }
        });
        MinecraftForge.EVENT_BUS.<RenderGuiEvent.Post>addListener(event -> {
            if (client.currentScreen == null && client.world != null && Blur.start > 0 && BlurConfig.INSTANCE.blurExclusions.get().stream().noneMatch(exclusion -> Blur.prevScreen.startsWith(exclusion)) && Blur.screenHasBackground) {
                event.getGuiGraphics().fillGradient(0, 0, event.getWindow().getScaledWidth(), event.getWindow().getScaledHeight(), Blur.getBackgroundColor(false, false), Blur.getBackgroundColor(true, false));
            }
        });
        MinecraftForge.EVENT_BUS.<ScreenEvent.BackgroundRendered>addListener(event -> Blur.screenHasBackground = true);
        MinecraftForge.EVENT_BUS.<ScreenEvent.Opening>addListener(event -> {
            if (event.getCurrentScreen() != null) {
                oldScreen = event.getCurrentScreen().getClass().getName();
            }

            Blur.onScreenChange(event.getNewScreen());
        });
        MinecraftForge.EVENT_BUS.<ScreenEvent.Closing>addListener(event -> Blur.onScreenChange(BlurConfig.INSTANCE.strangeEffect.get() ? null : Objects.equals(oldScreen, event.getScreen().getClass().getName()) ? event.getScreen() : null));
    }

    private static boolean doFade = false;

    public static void onScreenChange(Screen newGui) {
        String guiClassName = newGui == null ? null : newGui.getClass().getName();

        if (client.world != null) {
            boolean excluded = newGui == null || BlurConfig.INSTANCE.blurExclusions.get().parallelStream().anyMatch(guiClassName::startsWith);
            if (!excluded) {
                screenHasBackground = false;
                if (BlurConfig.INSTANCE.showScreenTitle.get()) System.out.println(guiClassName);
                blur.setUniformValue("Radius", BlurConfig.INSTANCE.radius.get().floatValue());
                if (doFade) {
                    start = System.currentTimeMillis();
                    doFade = false;
                }
                prevScreen = guiClassName;
            } else if (newGui == null && BlurConfig.INSTANCE.fadeOutTimeMillis.get() > 0 && !Objects.equals(prevScreen, "")) {
                blur.setUniformValue("Radius", BlurConfig.INSTANCE.radius.get().floatValue());
                start = System.currentTimeMillis();
                doFade = true;
            } else {
                screenHasBackground = false;
                start = -1;
                doFade = true;
                prevScreen = "";
            }
        }
    }

    private static float getProgress(boolean fadeIn) {
        float x;
        if (fadeIn) {
            x = Math.min((System.currentTimeMillis() - start) / (float) BlurConfig.INSTANCE.fadeTimeMillis.get(), 1);
            if (BlurConfig.INSTANCE.ease.get()) x *= (2 - x);  // easeInCubic
        }
        else {
            x = Math.max(1 + (start - System.currentTimeMillis()) / (float) BlurConfig.INSTANCE.fadeOutTimeMillis.get(), 0);
            if (BlurConfig.INSTANCE.ease.get()) x *= (2 - x);  // easeOutCubic
            if (x <= 0) {
                start = 0;
                screenHasBackground = false;
            }
        }
        return x;
    }

    public static int getBackgroundColor(boolean second, boolean fadeIn) {
        int a = second ? BlurConfig.INSTANCE.gradientEndAlpha.get() : BlurConfig.INSTANCE.gradientStartAlpha.get();
        var col = Color.decode(second ? BlurConfig.INSTANCE.gradientEnd.get() : BlurConfig.INSTANCE.gradientStart.get());
        int r = (col.getRGB() >> 16) & 0xFF;
        int b = (col.getRGB() >> 8) & 0xFF;
        int g = col.getRGB() & 0xFF;
        float prog = getProgress(fadeIn);
        a *= prog;
        r *= prog;
        g *= prog;
        b *= prog;
        return a << 24 | r << 16 | b << 8 | g;
    }
}
