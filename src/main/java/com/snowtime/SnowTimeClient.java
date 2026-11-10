package com.snowtime;

import net.fabricmc.api.ClientModInitializer;

public class SnowTimeClient implements ClientModInitializer {
    public static final String MOD_ID = "snowtime";

    @Override
    public void onInitializeClient() {
        System.out.println("[SNOW TIME] Мороз пришёл.");
    }
}
