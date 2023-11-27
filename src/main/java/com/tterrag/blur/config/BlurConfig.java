package com.tterrag.blur.config;

import com.google.common.collect.Lists;
import com.tterrag.blur.Blur;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Color;
import java.util.List;

public class BlurConfig {
    public static final Pair<BlurConfig, ForgeConfigSpec> CONFIG_SPEC_PAIR = new ForgeConfigSpec.Builder()
            .configure(BlurConfig::new);
    public static final BlurConfig INSTANCE = CONFIG_SPEC_PAIR.getKey();

    public final ForgeConfigSpec.ConfigValue<List<String>> blurExclusions;
    public final ForgeConfigSpec.IntValue fadeTimeMillis;
    public final ForgeConfigSpec.IntValue fadeOutTimeMillis;
    public final ForgeConfigSpec.BooleanValue ease;
    public final ForgeConfigSpec.IntValue radius;
    public final ForgeConfigSpec.ConfigValue<String> gradientStart;
    public final ForgeConfigSpec.IntValue gradientStartAlpha;
    public final ForgeConfigSpec.ConfigValue<String> gradientEnd;
    public final ForgeConfigSpec.IntValue gradientEndAlpha;
    public final ForgeConfigSpec.BooleanValue showScreenTitle;
    public final ForgeConfigSpec.BooleanValue strangeEffect;

    public BlurConfig(ForgeConfigSpec.Builder builder) {
        builder
                .translation("blur.midnightconfig.category.screens")
                .push("screens");

        blurExclusions = builder
                .translation("blur.midnightconfig.blurExclusions")
                .define("blurExclusions", () -> Lists.newArrayList(ChatScreen.class.getName(),
                        "com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiOverlay$UserInputGuiScreen",
                        "ai.arcblroth.projectInception.client.InceptionInterfaceScreen",
                        "net.optifine.gui.GuiChatOF",
                        "io.github.darkkronicle.advancedchatcore.chat.AdvancedChatScreen",
                        "net.coderbot.iris.gui.screen.ShaderPackScreen"),
                        o -> o != null && List.class.isAssignableFrom(o.getClass())
                );

        showScreenTitle = builder
                .translation("blur.midnightconfig.showScreenTitle")
                .define("showScreenTitle", false);

        strangeEffect = builder
                .translation("blur.midnightconfig.strangeEffect")
                .define("strangeEffect", false);

        builder.pop();


        builder
                .translation("blur.midnightconfig.category.style")
                .push("style");

        fadeTimeMillis = builder
                .translation("blur.midnightconfig.fadeTimeMillis")
                .defineInRange("fadeTimeMillis", 200, 0, 5000);

        fadeOutTimeMillis = builder
                .translation("blur.midnightconfig.fadeOutTimeMillis")
                .defineInRange("fadeOutTimeMillis", 200, 0, 5000);

        ease = builder
                .translation("blur.midnightconfig.ease")
                .define("ease", true);

        radius = builder
                .translation("blur.midnightconfig.radius")
                .defineInRange("radius", 8, 0, 100);

        gradientStart = builder
                .translation("blur.midnightconfig.gradientStart")
                .define("gradientStart", "#000000", BlurConfig::isValidColor);

        gradientStartAlpha = builder
                .translation("blur.midnightconfig.gradientStartAlpha")
                .defineInRange("gradientStartAlpha", 75, 0, 255);

        gradientEnd = builder
                .translation("blur.midnightconfig.gradientEnd")
                .define("gradientEnd", "#000000", BlurConfig::isValidColor);

        gradientEndAlpha = builder
                .translation("blur.midnightconfig.gradientEndAlpha")
                .defineInRange("gradientEndAlpha", 75, 0, 255);

        builder.pop();
    }

    static {
        ModContainer activeContainer = ModList.get().getModContainerById(Blur.MODID).get();

        activeContainer.addConfig(new ModConfig(ModConfig.Type.CLIENT, BlurConfig.CONFIG_SPEC_PAIR.getValue(), activeContainer));
    }

    private static boolean isValidColor(Object val) {
        try {
            Color.decode((String) val);

            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}