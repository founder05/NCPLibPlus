package me.marc_val_96.npclib.packetlistener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.marc_val_96.npclib.NPC;
import me.marc_val_96.npclib.NPCLib;
import me.marc_val_96.npclib.event.NPCInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class ProtocolLibListener implements PacketListener {

    private static final ArrayList<Player> playerswhointeract = new ArrayList<>();
    private static ProtocolManager protocolManager;

    public static void setup() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override public void startListening(Plugin plugin) {
        if (protocolManager == null) {
            setup();
        }
        protocolManager.addPacketListener(
            new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
                @Override public void onPacketReceiving(PacketEvent event) {
                    if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                        Player p = event.getPlayer();
                        try {
                            PacketContainer packet = event.getPacket();
                            int id = packet.getIntegers().read(0);
                            if (!playerswhointeract.contains(p)) {
                                for (NPCLib npc : NPC.getNPCs()) {
                                    if (npc.getEntityID(p) == id) {
                                        NPCInteractEvent interactevent =
                                            new NPCInteractEvent(p, npc);
                                        Bukkit.getPluginManager().callEvent(interactevent);
                                        break;
                                    }
                                }
                                playerswhointeract.add(p);
                                Bukkit.getScheduler().runTaskLaterAsynchronously(NPC.getPlugin(),
                                    () -> playerswhointeract.remove(p), 2);
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
    }
}
