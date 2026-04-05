package com.Adathan.systems;

import com.Adathan.interactions.SetupVoidAsylum;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import org.jspecify.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class AsylumDeathSystem extends DeathSystems.OnDeathSystem {
 private final ArrayList<String> bossNames;
 private final SetupVoidAsylum voidAsylumFunctions;

    public AsylumDeathSystem(@Nonnull ArrayList<String> bossNames, SetupVoidAsylum voidAsylumFunctions) {
        this.bossNames = bossNames;
        this.voidAsylumFunctions = voidAsylumFunctions;
    }

    @Override
    public @Nullable Query<EntityStore> getQuery() {
        return Archetype.empty();
    }

    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        NPCEntity npcEntityComponent = commandBuffer.getComponent(ref, NPCEntity.getComponentType());
        if (npcEntityComponent == null) { // player died
            return;
        }
        String roleName = npcEntityComponent.getRoleName().toLowerCase().strip();

        if (bossNames.contains(roleName)) {
            World world = npcEntityComponent.getWorld();
            NPCPlugin npcPlugin = NPCPlugin.get();
            switch (roleName.toLowerCase().strip()) {
                case "adathan_keyholder":
                    voidAsylumFunctions.spawnForbiddenPractitioner(world, npcPlugin);
                    break;
                case "adathan_forbidden_practitioner":
                    voidAsylumFunctions.spawnVoidbreaker(world, npcPlugin);
                    break;
                case "voidbreaker":
                    voidAsylumFunctions.spawnVoidDragon(world, npcPlugin);
                    break;
                case "void_dragon_asylum":
                    voidAsylumFunctions.spawnWarden(world, npcPlugin);
                    break;
                case "adathan_warden":
                    voidAsylumFunctions.wardenDeath(world);
                    break;
            }
        } // do nothing if it isn't our boss

    }
}

