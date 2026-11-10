package com.snowtime.mixin;

import com.snowtime.SnowTitleScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    // Подменяем ванильное главное меню на своё
    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true)
    private Screen snowtime$replaceTitle(Screen screen) {
        if (screen != null && screen.getClass() == TitleScreen.class) {
            return new SnowTitleScreen();
        }
        return screen;
    }
}
