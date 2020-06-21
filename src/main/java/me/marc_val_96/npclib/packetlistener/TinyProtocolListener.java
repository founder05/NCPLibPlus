package me.marc_val_96.npclib.packetlistener;

import io.netty.channel.Channel;
import me.marc_val_96.npclib.NPC;
import me.marc_val_96.npclib.NPCLib;
import me.marc_val_96.npclib.event.NPCInteractEvent;
import me.marc_val_96.npclib.tinyprotocol.Reflection;
import me.marc_val_96.npclib.tinyprotocol.TinyProtocol;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class TinyProtocolListener implements PacketListener {

    private static final Class<?> EntityInteractClass =
        Reflection.getClass("{nms}.PacketPlayInUseEntity");
    private static final Reflection.FieldAccessor<Integer> EntityID =
        Reflection.getField(EntityInteractClass, int.class, 0);
    private static final ArrayList<Player> playerswhointeract = new ArrayList<>();
    private static TinyProtocol protocol = null;

    @Override public void startListening(Plugin plugin) {
        if (protocol == null) {
            protocol = new TinyProtocol(plugin) {
                @Override
                public Object onPacketInAsync(Player sender, Channel channel, Object packet) {
                    if (EntityInteractClass.isInstance(packet)) {
                        if (!playerswhointeract.contains(sender)) {
                            for (NPCLib npc : NPC.getNPCs()) {
                                if (npc.getEntityID(sender) == EntityID.get(packet)) {
                                    NPCInteractEvent event = new NPCInteractEvent(sender, npc);
                                    Bukkit.getPluginManager().callEvent(event);
                                    break;
                                }
                            }
                            playerswhointeract.add(sender);
                            Bukkit.getScheduler().runTaskLaterAsynchronously(NPC.getPlugin(),
                                () -> playerswhointeract.remove(sender), 2);
                        }
                    }
                    return super.onPacketInAsync(sender, channel, packet);
                }
            };
        }
    }

}
