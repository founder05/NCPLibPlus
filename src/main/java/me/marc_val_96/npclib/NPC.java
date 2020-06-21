package me.marc_val_96.npclib;

import me.marc_val_96.npclib.nms.NPC_v1_15;
import me.marc_val_96.npclib.packetlistener.PacketListener;
import me.marc_val_96.npclib.packetlistener.ProtocolLibListener;
import me.marc_val_96.npclib.packetlistener.TinyProtocolListener;
import me.marc_val_96.npclib.protocol.ProtocolNPC;
import me.marc_val_96.npclib.skin.NPCSkin;
import net.minecraft.server.v1_15_R1.EntityInsentient;
import net.minecraft.server.v1_15_R1.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class NPC extends JavaPlugin {

    private static final ArrayList<NPCLib> npcs = new ArrayList<NPCLib>();
    private static String version;
    private static Plugin plugin;
    private static Boolean cache = true;

    private static PacketListener packetListener = null;


    public static Plugin getPlugin() {
        return plugin;
    }

    public static Boolean getCache() {
        return cache;
    }

    public static void useCache(boolean bol) {
        cache = bol;
    }

    private static void setupVersion() {
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
                .split(",")[3];
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public static void removeCacheFile() {
        if (version.equals("v1_15_R1")) {
            NPC_v1_15.removeCacheFile();
        }
    }

    public static ArrayList<NPCLib> getNPCs() {
        ArrayList<NPCLib> list = new ArrayList<NPCLib>();
        for (NPCLib npc : npcs) {
            if (!npc.isDeleted()) {
                list.add(npc);
            }
        }
        return list;
    }


    /**
     * Create a NPC
     *
     * @param plugin
     * @param location NPC Location
     * @param skin     NPC skin using a playername
     */

    public static NPCLib createNPC(Plugin plugin, Location location, NPCSkin skin) {
        NPC.plugin = plugin;
        if (version == null) {
            setupVersion();
        }
        if (packetListener == null) {
            if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
                packetListener = new ProtocolLibListener();
            } else {
                packetListener = new TinyProtocolListener();
            }
            packetListener.startListening(plugin);
        }
        if (packetListener instanceof ProtocolLibListener) {
            ProtocolNPC.startTask(plugin);
            NPCLib npc = new ProtocolNPC(location, skin);
            npcs.add(npc);
            return npc;
        }


        if (version.equals("v1_15_R1")) {
            NPC_v1_15.startTask(plugin);
            NPCLib npc = new NPC_v1_15(location, skin);
            npcs.add(npc);
            return npc;
        } else {
            Bukkit.getLogger().log(Level.SEVERE, ChatColor.RED + "Unsopported server version.");
            return null;
        }
    }

    @Override public void onEnable() {
        if (packetListener == null) {
            if (Bukkit.getPluginManager().getPlugin("ProtocolLib") != null) {
                packetListener = new ProtocolLibListener();
                Bukkit.getLogger().log(Level.INFO,
                    ChatColor.YELLOW + "ProtocolLib founded. NPCs listening and ussing it :)");
            }
        }
    }

    public void registerEntity(String name, int id, Class<? extends EntityInsentient> nmsClass,
        Class<? extends EntityInsentient> customClass) {
        try {

            List<Map<?, ?>> dataMap = new ArrayList<>();
            for (Field f : EntityTypes.class.getDeclaredFields()) {
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    f.setAccessible(true);
                    dataMap.add((Map<?, ?>) f.get(null));
                }
            }

            if (dataMap.get(2).containsKey(id)) {
                dataMap.get(0).remove(name);
                dataMap.get(2).remove(id);
            }

            Method method =
                EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(null, customClass, name, id);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
