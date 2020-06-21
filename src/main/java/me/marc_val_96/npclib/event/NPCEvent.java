package me.marc_val_96.npclib.event;

import me.marc_val_96.npclib.NPC;
import me.marc_val_96.npclib.NPCLib;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public abstract class NPCEvent extends Event {

    private  Player player;
    private NPCLib npc;

    public NPCEvent(Player player, NPCLib npc) {
        this.player = player;
        this.npc = npc;
    }

    public NPCEvent(NPC npc) {

    }

    protected NPCEvent() {
    }


    public Player getPlayer() {
        return this.player;
    }

    public NPCLib getNPC() {
        return npc;
    }
}
