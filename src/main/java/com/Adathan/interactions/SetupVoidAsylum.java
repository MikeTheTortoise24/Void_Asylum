package com.Adathan.interactions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.npc.INonPlayerCharacter;
import com.hypixel.hytale.server.core.universe.world.spawn.GlobalSpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.asset.builder.BuilderInfo;
import it.unimi.dsi.fastutil.Pair;

import javax.annotation.Nonnull;

public class SetupVoidAsylum extends SimpleInstantInteraction {
    public static final BuilderCodec CODEC;

    public Integer fightNumber = 0;

    // Environment Names
    protected static final String fpEnv = "Env_Forbidden_Practitioner";
    protected static final String vbEnv = "Env_Voidbreaker";
    protected static final String voidDragonEnv = "Env_Void_Dragon";
    protected static final String wardenEnv = "Env_Warden";

    // Forbidden Practitioner Anchor/Constants
    protected static final Double fpAnchorX = 0.5;
    protected static final Double fpAnchorY = 102.0;
    protected static final Double fpAnchorZ = -71.5;
    protected static final Double fpTeleporterBlockX = -1.0;
    protected static final Double fpTeleporterBlockY = 102.0;
    protected static final Double fpTeleporterBlockZ = -56.0;
    protected static final String fpTeleporterBlockName = "Void_Asylum_Boss_One_TP";

    protected static final Vector3d fpDeathBlockToRemove = new Vector3d(0, 102, -87);
    protected static final String fpDeathBlockToSetAs = "Furniture_Human_Ruins_Door";

    protected static final Vector3d fpDeathSpawnPointSet = new Vector3d(0.5, 102, -71.5);

    // Voidbreaker Anchor/Constants
    protected static final Double vbAnchorX = 0.5;
    protected static final Double vbAnchorY = 101.5;
    protected static final Double vbAnchorZ = -145.5;
    protected static final Double vbMarkerOffset = 21.0;
    protected static final Vector3d vbBossSpawnOffsetVector = new Vector3d(0.0, 0.5, -20.0);
    protected static final Double vbTeleporterBlockX = -1.0;
    protected static final Double vbTeleporterBlockY = 102.0;
    protected static final Double vbTeleporterBlockZ = -119.0;
    protected static final String vbTeleporterBlockName = "Void_Asylum_Boss_Two_TP";

    protected static final Vector3d vbDeathBlockToRemove = new Vector3d(0, 102, -172);
    protected static final String vbDeathBlockToSetAs = "Furniture_Human_Ruins_Door";

    protected static final Vector3d vbDeathBlockToSet2 = new Vector3d(0, 103, -223);
    protected static final String vbDeathBlockToSetAs2 = "Start_Void_Dragon_Fight";

    protected static final Vector3d vbDeathSpawnPointSet = new Vector3d(0.5, 102, -145.5);

    // Void Dragon Anchor/Constants
    // anchor is the bottom right center block
    protected static final Double dragonAnchorX = -17.0;
    protected static final Double dragonAnchorY = 9.0;
    protected static final Double dragonAnchorZ = -260.0;
    protected static final Double dragonSpearXOffset = 17.0;
    protected static final Double dragonSpearZOffset = 51.0;
    protected static final Double fallNegationZoneBlockX = 0.0;
    protected static final Double fallNegationZoneBlockY = 101.0;
    protected static final Double fallNegationZoneBlockZ = -224.0;
    protected static final String fallNegationZoneBlockName = "Fall_Negation_Zone";

    protected static final Vector3d dragonDeathBlockToSet = new Vector3d(23, 10, -243);
    protected static final String dragonDeathBlockToSetAs = "Void_Dragon_Launchpad";

    protected static final Vector3d dragonDeathSpawnPointSet = new Vector3d(0.5, 10, -234);

    // Warden Anchor/Constants
    protected static final Double wardenAnchorX = 149.5;
    protected static final Double wardenAnchorY = 94.5;
    protected static final Double wardenAnchorZ = -242.5;
    protected static final Double wardenMarkerOffset = 15.0;

    protected static final Vector3d wardenDeathBlockToSet = new Vector3d(149, 95, -257);
    protected static final String wardenDeathBlockToSetAs = "Portal_Return";

    protected static final Vector3d wardenDeathSpawnPointSet = new Vector3d(149.5, 95, -242.5);

    @Override
    protected void firstRun(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nonnull CooldownHandler cooldownHandler) {
        World world = interactionContext.getEntity().getStore().getExternalData().getWorld();
        NPCPlugin npcPlugin = NPCPlugin.get();

        switch (fightNumber) {
            case 0:
                spawnForbiddenPractitioner(world, npcPlugin);
                spawnVoidbreaker(world, npcPlugin);
                spawnVoidDragon(world, npcPlugin);
                spawnWarden(world, npcPlugin);
                break;
            case 1:
                spawnForbiddenPractitioner(world, npcPlugin);
                break;
            case 2:
                spawnVoidbreaker(world, npcPlugin);
                break;
            case 3:
                spawnVoidDragon(world, npcPlugin);
                break;
            case 4:
                spawnWarden(world, npcPlugin);
                break;

        }
    }

    public void spawnForbiddenPractitioner(World world, NPCPlugin npcPlugin) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        Vector3d centerPos = new Vector3d(fpAnchorX, fpAnchorY, fpAnchorZ);

        // spawn the portal to enter the Forbidden Practitioner's room
        try {
            world.setBlock((int) Math.floor(fpTeleporterBlockX), (int) Math.floor(fpTeleporterBlockY), (int) Math.floor(fpTeleporterBlockZ), fpTeleporterBlockName);
        } catch (Exception e) {
            HytaleLogger.getLogger().atWarning().log("[Void Asylum] Failed to set block %s: %s", fpTeleporterBlockName, e.getMessage());
        }

        // spawn and freeze the center room Boss_Marker_Platform
        world.execute(() -> {
            try {
                Vector3f markerRotation = new Vector3f(0, (float) Math.toRadians(-180), 0);
                Pair<Ref<EntityStore>, INonPlayerCharacter> result = npcPlugin.spawnNPC(store, "Boss_Marker_Platform", null, centerPos, markerRotation);
                Ref<EntityStore> npcRef = result.first();
                store.ensureComponent(npcRef, Frozen.getComponentType());
            } catch (Exception e) {
                HytaleLogger.getLogger().atWarning().log("[VoidAsylum] Failed to spawn Mob Boss_Marker_Platform at %s: %s", centerPos.toString(), e.getMessage());
            }
        });

        // spawn the Forbidden Practitioner in the center!
        world.execute(() -> {
            try {
                Vector3f bossRotation = new Vector3f(0, (float) Math.toRadians(-180), 0);
                int forbiddenPractitionerIndex = npcPlugin.getIndex("Adathan_Forbidden_Practitioner");
                BuilderInfo roleBuilderInfo = npcPlugin.getRoleBuilderInfo(forbiddenPractitionerIndex);
                npcPlugin.forceValidation(forbiddenPractitionerIndex);
                npcPlugin.testAndValidateRole(roleBuilderInfo);
                Pair<Ref<EntityStore>, INonPlayerCharacter> result = npcPlugin.spawnNPC(store, "Adathan_Forbidden_Practitioner", null, centerPos, bossRotation);
                Ref<EntityStore> npcRef = result.first();
            } catch (Exception e) {
                HytaleLogger.getLogger().atWarning().log("[VoidAsylum] Failed to spawn Mob Adathan_Forbidden_Practitioner at %s: %s", centerPos.toString(), e.getMessage());
            }
        });
    }

    public void spawnVoidbreaker(World world, NPCPlugin npcPlugin) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        Vector3d centerPos = new Vector3d(vbAnchorX, vbAnchorY, vbAnchorZ);

        // set the player's spawn point to the middle of the Forbidden Practitioner's room.
        Transform spawnTransform = new Transform(fpDeathSpawnPointSet.clone(), Vector3f.ZERO);
        WorldConfig worldConfig = world.getWorldConfig();
        worldConfig.setSpawnProvider(new GlobalSpawnProvider(spawnTransform));
        worldConfig.markChanged();

        // add the door in the Forbidden Practitioner's room to exit.
        try {
            world.setBlock((int) Math.floor(fpDeathBlockToRemove.getX()), (int) Math.floor(fpDeathBlockToRemove.getY()), (int) Math.floor(fpDeathBlockToRemove.getZ()), fpDeathBlockToSetAs);
        } catch (Exception e) {
            HytaleLogger.getLogger().atWarning().log("[Void Asylum] Failed to set block %s: %s", fpDeathBlockToSetAs, e.getMessage());
        }

        // spawn the portal to enter the Voidbreaker's room
        try {
            world.setBlock((int) Math.floor(vbTeleporterBlockX), (int) Math.floor(vbTeleporterBlockY), (int) Math.floor(vbTeleporterBlockZ), vbTeleporterBlockName);
        } catch (Exception e) {
            HytaleLogger.getLogger().atWarning().log("[Void Asylum] Failed to set block %s: %s", vbTeleporterBlockName, e.getMessage());
        }

        // spawn and freeze the center room Boss_Marker_Platform & Boss_Marker_Platform_Secondary
        world.execute(() -> {
            Vector3f markerRotation = Vector3f.ZERO;
            // Boss_Marker_Platform
            try {
                Pair<Ref<EntityStore>, INonPlayerCharacter> result = npcPlugin.spawnNPC(store, "Boss_Marker_Platform", null, centerPos, markerRotation);
                Ref<EntityStore> npcRef = result.first();
                store.ensureComponent(npcRef, Frozen.getComponentType());
            } catch (Exception e) {
                HytaleLogger.getLogger().atWarning().log("[VoidAsylum] Failed to spawn Mob Boss_Marker_Platform at %s: %s", centerPos.toString(), e.getMessage());
            }
            // Boss_Marker_Platform_Secondary
            try {
                Pair<Ref<EntityStore>, INonPlayerCharacter> result = npcPlugin.spawnNPC(store, "Boss_Marker_Platform_Secondary", null, centerPos, markerRotation);
                Ref<EntityStore> npcRef = result.first();
                store.ensureComponent(npcRef, Frozen.getComponentType());
            } catch (Exception e) {
                HytaleLogger.getLogger().atWarning().log("[VoidAsylum] Failed to spawn Mob Boss_Marker_Platform_Secondary at %s: %s", centerPos.toString(), e.getMessage());
            }
        });

        // spawn a Boss_Marker in every corner of the room + cardinal directions. Face them inward to the center
        for (int i = 0; i < 8; i++) {
            double angle = 2 * Math.PI * i / 8;

            double markerX = vbAnchorX + vbMarkerOffset * Math.cos(angle);
            double markerZ = vbAnchorZ + vbMarkerOffset * Math.sin(angle);
            double markerY = vbAnchorY; // keep same height

            double yawRadians = -(angle + Math.PI);
            yawRadians -= Math.PI / 2;

            Vector3f markerRotation = new Vector3f(0, (float) yawRadians, 0);

            Vector3d markerPos = new Vector3d(markerX, markerY, markerZ);
            world.execute(() -> {
                try {
                    Pair<Ref<EntityStore>, INonPlayerCharacter> result = npcPlugin.spawnNPC(store, "Boss_Marker", null, markerPos, markerRotation);
                    Ref<EntityStore> npcRef = result.first();
                    store.ensureComponent(npcRef, Frozen.getComponentType());
                } catch (Exception e) {
                    HytaleLogger.getLogger().atWarning().log("[VoidAsylum] Failed to spawn Mob Boss_Marker at %s: %s", markerPos.toString(), e.getMessage());
                }
            });
        }

        // spawn the Voidbreaker in the center!
        world.execute(() -> {
            try {
                Vector3d bossPosition = new Vector3d(vbAnchorX + vbBossSpawnOffsetVector.getX(), vbAnchorY + vbBossSpawnOffsetVector.getY(), vbAnchorZ + vbBossSpawnOffsetVector.getZ());
                int voidbreakerIndex = npcPlugin.getIndex("Voidbreaker");
                BuilderInfo roleBuilderInfo = npcPlugin.getRoleBuilderInfo(voidbreakerIndex);
                npcPlugin.forceValidation(voidbreakerIndex);
                npcPlugin.testAndValidateRole(roleBuilderInfo);
                Pair<Ref<EntityStore>, INonPlayerCharacter> result = npcPlugin.spawnNPC(store, "Voidbreaker", null, bossPosition, Vector3f.ZERO);
                Ref<EntityStore> npcRef = result.first();
            } catch (Exception e) {
                HytaleLogger.getLogger().atWarning().log("[VoidAsylum] Failed to spawn Mob Voidbreaker at %s: %s", centerPos.toString(), e.getMessage());
            }
        });
    }

    public void spawnVoidDragon(World world, NPCPlugin npcPlugin) {
        Store<EntityStore> store = world.getEntityStore().getStore();

        // set the player's spawn point to the middle of the Voidbreaker's room.
        Transform spawnTransform = new Transform(vbDeathSpawnPointSet.clone(), Vector3f.ZERO);
        WorldConfig worldConfig = world.getWorldConfig();
        worldConfig.setSpawnProvider(new GlobalSpawnProvider(spawnTransform));
        worldConfig.markChanged();

        // add the door in the Voidbreaker's room to exit.
        try {
            world.setBlock((int) Math.floor(vbDeathBlockToRemove.getX()), (int) Math.floor(vbDeathBlockToRemove.getY()), (int) Math.floor(vbDeathBlockToRemove.getZ()), vbDeathBlockToSetAs);
        } catch (Exception e) {
            HytaleLogger.getLogger().atWarning().log("[Void Asylum] Failed to set block %s: %s", vbDeathBlockToSetAs, e.getMessage());
        }

        // spawn fall_negation_zone (doesn't work in the prefab)
        try {
            world.setBlock((int) Math.floor(fallNegationZoneBlockX), (int) Math.floor(fallNegationZoneBlockY), (int) Math.floor(fallNegationZoneBlockZ), fallNegationZoneBlockName);
        } catch (Exception e) {
            HytaleLogger.getLogger().atWarning().log("[Void Asylum] Failed to set block %s: %s", fallNegationZoneBlockName, e.getMessage());
        }

        // spawn fall zone (doesn't work in once placed from the prefab)
        try {
            world.setBlock((int) Math.floor(vbDeathBlockToSet2.getX()), (int) Math.floor(vbDeathBlockToSet2.getY()), (int) Math.floor(vbDeathBlockToSet2.getZ()), vbDeathBlockToSetAs2);
        } catch (Exception e) {
            HytaleLogger.getLogger().atWarning().log("[Void Asylum] Failed to set block %s: %s", vbDeathBlockToSetAs2, e.getMessage());
        }

        // spawn and freeze the platform NPCs
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                Vector3d floorPosition = new Vector3d(dragonAnchorX + (17 * i), dragonAnchorY + .5, dragonAnchorZ + (17 * j));
                world.execute(() -> {
                    try {
                        Pair<Ref<EntityStore>, INonPlayerCharacter> result = npcPlugin.spawnNPC(store, "Boss_Marker_Platform", null, floorPosition, Vector3f.ZERO);
                        Ref<EntityStore> npcRef = result.first();
                        store.ensureComponent(npcRef, Frozen.getComponentType());
                    } catch (Exception e) {
                        HytaleLogger.getLogger().atWarning().log("[VoidAsylum] Failed to spawn Mob Boss_Marker_Platform at %s: %s", floorPosition.toString(), e.getMessage());
                    }
                });
            }
        }

        // spawn and freeze the spear NPCs
        for (int i = 0; i < 3; ++i) {
            Vector3d spearPosition = new Vector3d(dragonAnchorX + (dragonSpearXOffset * i), dragonAnchorY + 4, dragonAnchorZ + dragonSpearZOffset);
            world.execute(() -> {
                try {
                    Pair<Ref<EntityStore>, INonPlayerCharacter> result = npcPlugin.spawnNPC(store, "Boss_Marker_Spear", null, spearPosition, Vector3f.ZERO);
                    Ref<EntityStore> npcRef = result.first();
                    store.ensureComponent(npcRef, Frozen.getComponentType());
                } catch (Exception e) {
                    HytaleLogger.getLogger().atWarning().log("[VoidAsylum] Failed to spawn Mob Boss_Marker_Spear at %s: %s", spearPosition.toString(), e.getMessage());
                }
            });
        }

        for (int i = 0; i < 3; ++i) {
            Vector3d spearPosition = new Vector3d(dragonAnchorX + dragonSpearZOffset, dragonAnchorY + 4, dragonAnchorZ + (dragonSpearXOffset * i));
            world.execute(() -> {
                try {
                    //1.5708f is 90 degrees in rads
                    Pair<Ref<EntityStore>, INonPlayerCharacter> result = npcPlugin.spawnNPC(store, "Boss_Marker_Spear", null, spearPosition, new Vector3f(0, (float) Math.toRadians(90), 0));
                    Ref<EntityStore> npcRef = result.first();
                    store.ensureComponent(npcRef, Frozen.getComponentType());
                } catch (Exception e) {
                    HytaleLogger.getLogger().atWarning().log("[VoidAsylum] Failed to spawn Mob Boss_Marker_Spear at %s: %s", spearPosition.toString(), e.getMessage());
                }
            });
        }

        // spawn the dragon in the center!
        Vector3d centerPos = new Vector3d(dragonAnchorX + 17, dragonAnchorY + 2, dragonAnchorZ + 17);

        world.execute(() -> {
            try {
                int voidDragonIndex = npcPlugin.getIndex("Void_Dragon_Asylum");
                BuilderInfo roleBuilderInfo = npcPlugin.getRoleBuilderInfo(voidDragonIndex);
                npcPlugin.forceValidation(voidDragonIndex);
                npcPlugin.testAndValidateRole(roleBuilderInfo);
                Pair<Ref<EntityStore>, INonPlayerCharacter> result = npcPlugin.spawnNPC(store, "Void_Dragon_Asylum", null, centerPos, Vector3f.ZERO);
                Ref<EntityStore> npcRef = result.first();
            } catch (Exception e) {
                HytaleLogger.getLogger().atWarning().log("[VoidAsylum] Failed to spawn Mob Void_Dragon_Asylum at %s: %s", centerPos.toString(), e.getMessage());
            }
        });
    }

    public void spawnWarden(World world, NPCPlugin npcPlugin) {
        Store<EntityStore> store = world.getEntityStore().getStore();
        Vector3d centerPos = new Vector3d(wardenAnchorX, wardenAnchorY, wardenAnchorZ);

        // set the player's spawn point to the middle of the Voidbreaker's room.
        Transform spawnTransform = new Transform(dragonDeathSpawnPointSet.clone(), Vector3f.ZERO);
        WorldConfig worldConfig = world.getWorldConfig();
        worldConfig.setSpawnProvider(new GlobalSpawnProvider(spawnTransform));
        worldConfig.markChanged();

        // re-add the platform that the launch pad will be on in case it is destroyed
        Vector3d position = new Vector3d(17, 9, -243);
        String blockName = "Rock_Magma_Cooled_Brick_Smooth";
        for (int i = -7; i <= 7; ++i) {
            for (int j = -7; j <= 7; ++j) {
                Vector3d blockPosition = new Vector3d(position.x + i, position.y, position.z + j);
                try {
                    world.setBlock((int) Math.floor(blockPosition.x), (int) Math.floor(blockPosition.y), (int) Math.floor(blockPosition.z), blockName);
                } catch (Exception e) {
                    HytaleLogger.getLogger().atWarning().log("[Void Asylum] Failed to set block %s: %s", blockName, e.getMessage());
                }
            }
        }

        // add the launch mechanics in the Void Dragon's room to exit.
        try {
            world.setBlock((int) Math.floor(dragonDeathBlockToSet.getX()), (int) Math.floor(dragonDeathBlockToSet.getY()), (int) Math.floor(dragonDeathBlockToSet.getZ()), dragonDeathBlockToSetAs);
        } catch (Exception e) {
            HytaleLogger.getLogger().atWarning().log("[Void Asylum] Failed to set block %s: %s", dragonDeathBlockToSetAs, e.getMessage());
        }

        // spawn a Boss_Marker_Platform in every cardinal direction. Get them to face inward
        for (int i = 0; i < 4; i++) {
            double angle = i * (Math.PI / 2);

            double markerX = wardenAnchorX + wardenMarkerOffset * Math.cos(angle);
            double markerZ = wardenAnchorZ + wardenMarkerOffset * Math.sin(angle);
            double markerY = wardenAnchorY; // keep same height

            // angle towards center platform
            double yawRadians = -(angle + Math.PI);
            yawRadians -= Math.PI / 2;

            Vector3f markerRotation = new Vector3f(0, (float) yawRadians, 0);

            Vector3d markerPos = new Vector3d(markerX, markerY + 0.4, markerZ);
            world.execute(() -> {
                try {
                    Pair<Ref<EntityStore>, INonPlayerCharacter> result = npcPlugin.spawnNPC(store, "Boss_Marker_Platform", null, markerPos, markerRotation);
                    Ref<EntityStore> npcRef = result.first();
                    store.ensureComponent(npcRef, Frozen.getComponentType());
                } catch (Exception e) {
                    HytaleLogger.getLogger().atWarning().log("[VoidAsylum] Failed to spawn Mob Boss_Marker_Platform at %s: %s", markerPos.toString(), e.getMessage());
                }
            });
        }

        // spawn the Warden in the center!
        world.execute(() -> {
            try {
                Vector3d bossPosition = new Vector3d(wardenAnchorX, wardenAnchorY + 0.5, wardenAnchorZ);
                int wardenIndex = npcPlugin.getIndex("Adathan_Warden");
                BuilderInfo roleBuilderInfo = npcPlugin.getRoleBuilderInfo(wardenIndex);
                npcPlugin.forceValidation(wardenIndex);
                npcPlugin.testAndValidateRole(roleBuilderInfo);
                Pair<Ref<EntityStore>, INonPlayerCharacter> result = npcPlugin.spawnNPC(store, "Adathan_Warden", null, bossPosition, Vector3f.ZERO);
                Ref<EntityStore> npcRef = result.first();
            } catch (Exception e) {
                HytaleLogger.getLogger().atWarning().log("[VoidAsylum] Failed to spawn Mob Adathan_Warden at %s: %s", centerPos.toString(), e.getMessage());
            }
        });

    }

    public void wardenDeath(World world) {
        // set the player's spawn point to the middle of the Warden's platform.
        Transform spawnTransform = new Transform(wardenDeathSpawnPointSet.clone(), Vector3f.ZERO);
        WorldConfig worldConfig = world.getWorldConfig();
        worldConfig.setSpawnProvider(new GlobalSpawnProvider(spawnTransform));
        worldConfig.markChanged();

        // add the exit portal on the Warden's platform to exit.
        try {
            world.setBlock((int) Math.floor(wardenDeathBlockToSet.getX()), (int) Math.floor(wardenDeathBlockToSet.getY()), (int) Math.floor(wardenDeathBlockToSet.getZ()), wardenDeathBlockToSetAs);
        } catch (Exception e) {
            HytaleLogger.getLogger().atWarning().log("[Void Asylum] Failed to set block %s: %s", wardenDeathBlockToSetAs, e.getMessage());
        }
    }

    protected void simulateFirstRun(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nonnull CooldownHandler cooldownHandler) {
    }

    static {
        CODEC = BuilderCodec.builder(SetupVoidAsylum.class, SetupVoidAsylum::new, SimpleInstantInteraction.CODEC)
                .documentation("Sets up the Void Asylum fight. FightNumber corresponds to the boss number. 0 is all")
                .append(new KeyedCodec<>("FightNumber", Codec.INTEGER),
                        (ExecuteInteraction, o) -> ExecuteInteraction.fightNumber =(Integer) o,
                        (ExecuteInteraction) -> ExecuteInteraction.fightNumber)
                .documentation("Fight Number to Spawn").addValidator(Validators.nonNull()).add()
                .build();
    }
}
