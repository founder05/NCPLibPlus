package me.marc_val_96.npclib.nms;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.marc_val_96.npclib.NPC;
import me.marc_val_96.npclib.NPCLib;
import me.marc_val_96.npclib.enums.NPCAnimation;
import me.marc_val_96.npclib.event.NPCDespawnEvent;
import me.marc_val_96.npclib.event.NPCSpawnEvent;
import me.marc_val_96.npclib.enums.EquipmentSlot;
import me.marc_val_96.npclib.skin.*;
import me.marc_val_96.npclib.utils.StringUtils;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.*;

public class NPC_v1_15 implements NPCLib {



    private static final List<NPC_v1_15> npcs = new ArrayList<NPC_v1_15>();
    private static int id = 0;
    private static boolean taskstarted = false;
    private static Plugin plugin;
    private final int npcid;
    private final Location location;
    private final NPCSkin skin;
    private final List<Player> rendered = new ArrayList<Player>();
    private final List<Player> waiting = new ArrayList<Player>();
    private final HashMap<Player, EntityPlayer> player_entity = new HashMap<Player, EntityPlayer>();
    private final HashMap<Player, SkinData> player_cache = new HashMap<Player, SkinData>();
    private boolean deleted = false;
    private GameProfile gameprofile;

    public NPC_v1_15(Location location, NPCSkin skin) {
        npcid = id++;
        this.skin = skin;
        this.location = location;
        if (!npcs.contains(this)) {
            npcs.add(this);
        }
    }

    public static void startTask(Plugin plugin) {
        if (!taskstarted) {
            taskstarted = true;
            NPC_v1_15.plugin = plugin;
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override public void run() {
                    for (NPC_v1_15 nmsnpc : npcs) {
                        for (Player pl : Bukkit.getOnlinePlayers()) {
                            if (Objects.equals(nmsnpc.location.getWorld(), pl.getWorld())) {
                                if (nmsnpc.location.distance(pl.getLocation()) > 60
                                    && nmsnpc.rendered.contains(pl)) {
                                    nmsnpc.destroy(pl);
                                } else if (nmsnpc.location.distance(pl.getLocation()) < 60
                                    && !nmsnpc.rendered.contains(pl)) {
                                    if (!nmsnpc.waiting.contains(pl)) {
                                        nmsnpc.waiting.add(pl);
                                        nmsnpc.spawn(pl);
                                    }
                                }
                            } else {
                                nmsnpc.destroy(pl);
                            }
                        }
                    }
                }
            }, 0, 30);

            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
                @Override public void run() {
                    for (NPC_v1_15 nmsnpc : npcs) {
                        for (Player pl : Bukkit.getOnlinePlayers()) {
                            nmsnpc.destroy(pl);
                        }
                    }
                }
            }, 20 * (60 * 5), 20 * (60 * 5));
        }
    }

    public static void removeCacheFile() {
        File file = new File(plugin.getDataFolder().getPath() + "/npcdata.json");
        if (file.exists()) {
            file.delete();
        }
    }

    private void setValue(Object obj, String name, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(name);
            field.setAccessible(true);
            field.set(obj, value);
            field.setAccessible(false);
        } catch (Exception ignored) {
        }
    }

    private void sendPacket(Packet<?> packet, Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    @Override public Location getLocation() {
        return this.location;
    }

    @Override public int getEntityID(Player p) {
        if (player_entity.get(p) != null) {
            return player_entity.get(p).getId();
        } else {
            return -1;
        }
    }

    @Override public boolean isDeleted() {
        return deleted;
    }

    @Override public int getNpcID() {
        return npcid;
    }

    private String getRandomString(int lenght) {
        StringBuilder randStr = new StringBuilder();
        long milis = new GregorianCalendar().getTimeInMillis();
        Random r = new Random(milis);
        int i = 0;
        while (i < lenght) {
            char c = (char) r.nextInt(255);
            if ((c >= '0' && c <= '9') || (c >= 'A' && c <= 'Z')) {
                randStr.append(c);
                i++;
            }
        }
        return randStr.toString();
    }

    private JsonObject getChacheFile(Plugin plugin) {
        File file = new File(plugin.getDataFolder().getPath() + "/npcdata.json");
        if (file.exists()) {
            try {
                JsonParser parser = new JsonParser();
                JsonElement jsonElement = parser.parse(new FileReader(file));
                return jsonElement.getAsJsonObject();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        } else
            return null;
    }

    private JsonSkinData getCachedSkin() {
        if (NPC.getCache() && this.skin.getSkinType() != SkinType.PLAYER) {
            JsonObject jsonFile = getChacheFile(plugin);
            JsonArray skindata = null;
            try {
                assert jsonFile != null;
                skindata = jsonFile.getAsJsonArray("skindata");
            } catch (Exception ignored) {

            }
            JsonSkinData skin = null;
            if (skindata != null) {
                Iterator it = skindata.iterator();
                while (it.hasNext()) {
                    JsonElement element = (JsonElement) it.next();
                    if (element.getAsJsonObject().get("id").getAsInt() == this.npcid) {
                        String value = element.getAsJsonObject().get("value").getAsString();
                        String signature = element.getAsJsonObject().get("signature").getAsString();
                        long updated = element.getAsJsonObject().get("updated").getAsLong();
                        SkinData data = new SkinData(value, signature);
                        skin = new JsonSkinData(data, updated);
                    }
                }
            }
            return skin;
        }
        return null;
    }

    private void cacheSkin(SkinData skindata) {
        if (NPC.getCache() && this.skin.getSkinType() != SkinType.PLAYER) {
            JsonObject jsonFile = getChacheFile(plugin);
            JsonArray newskindata = new JsonArray();
            if (jsonFile != null) {
                JsonArray oldskindata = jsonFile.getAsJsonArray("skindata");
                for (JsonElement element : oldskindata) {
                    if (element.getAsJsonObject().get("id").getAsInt() == this.npcid) {
                    } else {
                        newskindata.add(element);
                    }
                }
            }
            JsonObject skin = new JsonObject();
            Date actualdate = new Date();
            skin.addProperty("id", this.npcid);
            skin.addProperty("value", skindata.getValue());
            skin.addProperty("signature", skindata.getSignature());
            skin.addProperty("updated", actualdate.getTime());
            newskindata.add(skin);

            JsonObject obj = new JsonObject();
            obj.add("skindata", newskindata);
            try {
                plugin.getDataFolder().mkdir();
                File file = new File(plugin.getDataFolder().getPath() + "/npcdata.json");
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write(obj.toString());
                writer.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private GameProfile getGameProfile(String profilename, SkinData skindata) {
        GameProfile profile;
        if (skindata != null) {
            profile = new GameProfile(UUID.randomUUID(), profilename);
            profile.getProperties().put("textures",
                new Property("textures", skindata.getValue(), skindata.getSignature()));
        } else {
            profile = new GameProfile(UUID.fromString("d7ca8c2c-6ebf-4b91-9b90-25886436e673"),
                profilename);
            profile.getProperties().put("textures", new Property("textures",
                "ewogICJ0aW1lc3RhbXAiIDogMTU5MjY0MTEzMjU5MywKICAicHJvZmlsZUlkIiA6ICJkN2NhOGMyYzZlYmY0YjkxOWI5MDI1ODg2NDM2ZTY3MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJNYXJjeDk2IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2NkYzFmY2UzZTdhN2JhNDE3NDdhMDIzMjNiMTBiZTAxOTYzMGQ0MTM1MWY4ZWQ2OTU1YzNiMzk4OWFiZjIxNTkiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==",
                "uyPT3AePHceCnbNmgpneAFkOA/UUGjFDntkkWjiLZ+EtQlP87DXrChV0coV63Qqxh1e+VXT+mmebv0NLAb4/1GHuR2So54pG5W9613tIq5i9u6nkn6NkqScsJxzKQk9/NfI3dnRwFzey4EzyUB6BhP+Frxn7H8l5Agf/RGziuHiNLE+OlF5OR72+QglZVILtpCpUV5XyYLRWlo4ZPIaPi8rORHe2gbw0K/Ig0qSFmJn91M6FWaaZ/UfGTcrgaofcKfSu3ofdGfw1gkMHKQzKBkN27v/le+p3UXIoUzdYqw8/GvJLJdsnA/oUqmJ6aUeWUOjFDLugrCsC7hV1Wbx1/BrTxGcJGbjxEw+8qsBCSThCZkyBzP2gjc1sqQtiv9iQkc56h1shMmnOVPKRhtWl+J0J+005TmFXhuqmR7Xn4vWmVWwQINJLKzJ8ltrGg821i5CByihnU0Pt33dxxbQJ4Aj8shHf1UJpFglMMGj+KX0wx2lZD1XdPsmMnPuFJG/+UAHOjQoOTTwynQ2lwaAgIqpbQl/FM+473a/oGR9QYOeSaXqmy7yrQuTukA6gzhodZpAwLjGCUtXHpo2Hl+yzAObI6tdm8YDZ/gWQ912p4IAIES+DpfYvCBZE9sROmv1h9OiUOv00QWo6ePZmhWmtWZrFB7cJNrjAMkyKcUdCiGQ="));

        }
        return profile;
    }

    @Override public NPCSkin getSkin() {
        return this.skin;
    }

    @Override public Player getBukkitEntity() {
        return null;
    }

    @Override public boolean isGodmode() {
        return false;
    }

    @Override public void setGodmode(boolean invulnerable) {

    }

    @Override public boolean isGravity() {
        return false;
    }

    @Override public void setGravity(boolean gravity) {

    }

    @Override public void setLying(double x, double y, double z) {

    }

    @Override public boolean isLying() {
        return false;
    }

    @Override public boolean pathfindTo(Location location) {
        return false;
    }

    @Override public boolean pathfindTo(Location location, double speed) {
        return false;
    }

    @Override public boolean pathfindTo(Location location, double speed, double range) {
        return false;
    }

    @Override public Entity getTarget() {
        return null;
    }

    @Override public void setTarget(Entity target) {

    }

    @Override public void lookAt(Location location) {

    }

    @Override public void setYaw(float yaw) {

    }

    @Override public void playAnimation(NPCAnimation animation) {

    }

    @Override public void setEquipment(EquipmentSlot slot, ItemStack item) {

    }

    @Override public boolean getEntityCollision() {
        return false;
    }

    @Override public void setEntityCollision(boolean entityCollision) {

    }

    @Override public boolean isCollisionEnabled() {
        return false;
    }

    @Override public void setCollisionEnabled(boolean collisionEnabled) {

    }

    @Override public void delete() {
        npcs.remove(this);
        for (Player p : Bukkit.getOnlinePlayers()) {
            destroy(p);
        }
        this.deleted = true;
    }

    private void setGameProfile(GameProfile profile) {
        this.gameprofile = profile;
    }

    private void spawn(Player p) {
        Date actualdate = new Date();
        JsonSkinData cachedskin = getCachedSkin();
        if (cachedskin == null || (((actualdate.getTime()) - (getCachedSkin().getTimeUpdated()))
            >= 518400)) {
            if (this.skin.getSkinType() == SkinType.PLAYER) {
                this.skin.getSkinDataAsync(new SkinDataReply() {
                    @Override public void done(SkinData skinData) {
                        GameProfile profile =
                            getGameProfile(StringUtils.getRandomString(), skinData);
                        if (skinData != null) {
                            if (player_cache.containsKey(p)) {
                                player_cache.replace(p, skinData);
                            } else {
                                player_cache.put(p, skinData);
                            }
                        } else {
                            profile = getGameProfile(StringUtils.getRandomString(), null);
                            if (player_cache.containsKey(p)) {
                                profile = getGameProfile(StringUtils.getRandomString(),
                                    player_cache.get(p));
                            }
                        }
                        spawnEnttity(p, profile);
                    }
                }, p);
            } else {
                this.skin.getSkinDataAsync(new SkinDataReply() {
                    @Override public void done(SkinData skinData) {
                        GameProfile profile =
                            getGameProfile(StringUtils.getRandomString(), skinData);
                        if (skinData != null) {
                            setGameProfile(profile);
                            cacheSkin(skinData);
                        } else {
                            profile = getGameProfile(StringUtils.getRandomString(), null);
                            setGameProfile(profile);
                        }
                        spawnEnttity(p);
                    }
                });
            }
        } else {
            GameProfile profile =
                getGameProfile(StringUtils.getRandomString(), cachedskin.getSkinData());
            setGameProfile(profile);
            spawnEnttity(p);
        }
    }

    private void spawnEnttity(Player p) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) getLocation().getWorld()).getHandle();
        EntityPlayer npcentity = new EntityPlayer(nmsServer, nmsWorld, this.gameprofile,
            new PlayerInteractManager(nmsWorld));
        npcentity.setLocation(location.getX(), location.getY(), location.getZ(),
            (byte) location.getYaw(), (byte) location.getPitch());
        if (this.player_entity.containsKey(p)) {
            this.player_entity.replace(p, npcentity);
        } else {
            this.player_entity.put(p, npcentity);
        }
        PacketPlayOutNamedEntitySpawn spawnpacket = new PacketPlayOutNamedEntitySpawn(npcentity);
        DataWatcher watcher = npcentity.getDataWatcher();
        watcher.set(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0xFF);
        setValue(spawnpacket, "h", watcher);

        PacketPlayOutScoreboardTeam scbpacket = new PacketPlayOutScoreboardTeam();
        try {
            Collection<String> plys = Lists.newArrayList();
            plys.add(gameprofile.getName());
            setValue(scbpacket, "i", 0);
            setValue(scbpacket, "b", this.gameprofile.getName());
            setValue(scbpacket, "a", this.gameprofile.getName());
            setValue(scbpacket, "e", "never");
            setValue(scbpacket, "j", 1);
            setValue(scbpacket, "h", plys);
            sendPacket(scbpacket, p);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        addToTablist(p, npcentity);
        sendPacket(spawnpacket, p);

        PacketPlayOutEntityHeadRotation rotationpacket = new PacketPlayOutEntityHeadRotation();
        setValue(rotationpacket, "a", npcentity.getId());
        setValue(rotationpacket, "b", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        sendPacket(rotationpacket, p);
        Bukkit.getScheduler().runTaskLater(NPC_v1_15.plugin, new Runnable() {
            @Override public void run() {
                rmvFromTablist(p, npcentity);
            }
        }, 26);
        this.rendered.add(p);
        this.waiting.remove(p);
        NPCSpawnEvent event = new NPCSpawnEvent(p, this);
        Bukkit.getPluginManager().callEvent(event);
    }

    private void spawnEnttity(Player p, GameProfile profile) {
        MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) getLocation().getWorld()).getHandle();
        EntityPlayer npcentity =
            new EntityPlayer(nmsServer, nmsWorld, profile, new PlayerInteractManager(nmsWorld));
        npcentity.setLocation(location.getX(), location.getY(), location.getZ(),
            (byte) location.getYaw(), (byte) location.getPitch());
        if (this.player_entity.containsKey(p)) {
            this.player_entity.replace(p, npcentity);
        } else {
            this.player_entity.put(p, npcentity);
        }
        PacketPlayOutNamedEntitySpawn spawnpacket = new PacketPlayOutNamedEntitySpawn(npcentity);
        DataWatcher watcher = npcentity.getDataWatcher();
        watcher.set(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte) 0xFF);
        setValue(spawnpacket, "h", watcher);

        PacketPlayOutScoreboardTeam scbpacket = new PacketPlayOutScoreboardTeam();
        try {
            Collection<String> plys = Lists.newArrayList();
            plys.add(gameprofile.getName());
            setValue(scbpacket, "i", 0);
            setValue(scbpacket, "b", profile.getName());
            setValue(scbpacket, "a", profile.getName());
            setValue(scbpacket, "e", "never");
            setValue(scbpacket, "j", 1);
            setValue(scbpacket, "h", plys);
            sendPacket(scbpacket, p);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        addToTablist(p, npcentity);
        sendPacket(spawnpacket, p);

        PacketPlayOutEntityHeadRotation rotationpacket = new PacketPlayOutEntityHeadRotation();
        setValue(rotationpacket, "a", npcentity.getId());
        setValue(rotationpacket, "b", (byte) ((int) (location.getYaw() * 256.0F / 360.0F)));
        sendPacket(rotationpacket, p);
        Bukkit.getScheduler().runTaskLater(NPC_v1_15.plugin, new Runnable() {
            @Override public void run() {
                rmvFromTablist(p, npcentity);
            }
        }, 26);
        this.rendered.add(p);
        this.waiting.remove(p);
        NPCSpawnEvent event = new NPCSpawnEvent(p, this);
        Bukkit.getPluginManager().callEvent(event);
    }

    private void destroy(Player p) {
        EntityPlayer npcentity = null;
        try {
            if (this.player_entity.get(p) != null) {
                npcentity = this.player_entity.get(p);
            }

            PacketPlayOutScoreboardTeam removescbpacket = new PacketPlayOutScoreboardTeam();
            assert npcentity != null;
            setValue(removescbpacket, "a", npcentity.getProfile().getName());
            setValue(removescbpacket, "i", 1);
            sendPacket(removescbpacket, p);

            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(npcentity.getId());
            sendPacket(packet, p);

            this.rendered.remove(p);
            NPCDespawnEvent event = new NPCDespawnEvent(p, this);
            Bukkit.getPluginManager().callEvent(event);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void addToTablist(Player p, EntityPlayer npcentity) {
        PacketPlayOutPlayerInfo packet =
            new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                npcentity);
        sendPacket(packet, p);
    }

    private void rmvFromTablist(Player p, EntityPlayer npcentity) {
        PacketPlayOutPlayerInfo packet =
            new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                npcentity);
        sendPacket(packet, p);
    }

}

