package me.marc_val_96.npclib.event;

import me.marc_val_96.npclib.NPCLib;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class NPCInteractEvent extends NPCEvent {

    private static final HandlerList handlers = new HandlerList();

    public NPCInteractEvent(Player player, NPCLib npc) {
        super(player, npc);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

}
