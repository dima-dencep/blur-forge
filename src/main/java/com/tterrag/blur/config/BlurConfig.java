package com.tterrag.blur.config;

import com.google.common.collect.Lists;
import com.tterrag.blur.Blur;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.minecraft.client.gui.screen.ChatScreen;

import java.util.List;

@Config(name = Blur.MODID)
public class BlurConfig implements ConfigData {
    @ConfigEntry.Category("screens")
    public List<String> blurExclusions = Lists.newArrayList(ChatScreen.class.getName(),
            "com.replaymod.lib.de.johni0702.minecraft.gui.container.AbstractGuiOverlay$UserInputGuiScreen",
            "ai.arcblroth.projectInception.client.InceptionInterfaceScreen",
            "net.optifine.gui.GuiChatOF",
            "io.github.darkkronicle.advancedchatcore.chat.AdvancedChatScreen",
            "net.coderbot.iris.gui.screen.ShaderPackScreen");

    @ConfigEntry.Category("style")
    @ConfigEntry.BoundedDiscrete(max = 5000)
    public int fadeTimeMillis = 200;

    @ConfigEntry.Category("style")
    public boolean ease = true;

    @ConfigEntry.Category("style")
    @ConfigEntry.BoundedDiscrete(max = 100)
    public float radius = 8;

    @ConfigEntry.Category("style")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int gradientStart = 0x000000;

    @ConfigEntry.Category("style")
    @ConfigEntry.ColorPicker(allowAlpha = true)
    public int gradientEnd = 0x000000;

    @ConfigEntry.Category("screens")
    public boolean showScreenTitle = false;
}