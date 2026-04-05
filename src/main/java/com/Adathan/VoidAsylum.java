package com.Adathan;

import com.Adathan.interactions.SetupVoidAsylum;
import com.Adathan.systems.AsylumDeathSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import java.util.ArrayList;
import java.util.Arrays;

public class VoidAsylum extends JavaPlugin {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final ArrayList<String> bossNames = new ArrayList<>(Arrays.asList("adathan_keyholder", "adathan_forbidden_practitioner", "voidbreaker", "void_dragon_asylum", "adathan_warden"));

    public VoidAsylum(JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        this.getLogger().atInfo().log("[VoidAsylum] Starting Plugin!");
        new HStats("HStats_Code", this.getManifest().getVersion().toString());
        Interaction.CODEC.register("SetupVoidAsylum", SetupVoidAsylum.class, SetupVoidAsylum.CODEC);
        SetupVoidAsylum voidAsylumFunctions = new SetupVoidAsylum();
        this.getEntityStoreRegistry().registerSystem(new AsylumDeathSystem(bossNames, voidAsylumFunctions));
    }
}
