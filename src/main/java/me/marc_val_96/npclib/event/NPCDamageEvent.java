package me.marc_val_96.npclib.event;

import me.marc_val_96.npclib.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;


public class NPCDamageEvent extends NPCEvent implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    private final NPC npc;
    private final Entity damager;
    private final DamageCause cause;
    private boolean cancelled = false;
    private double damage;

    public NPCDamageEvent(NPC npc, Entity damager, DamageCause cause, double damage) {
        this.npc = npc;
        this.damager = damager;
        this.cause = cause;
        this.damage = damage;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override public boolean isCancelled() {
        return cancelled;
    }

    @Override public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public DamageCause getCause() {
        return cause;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public NPC getNpc() {
        return npc;
    }

    public Entity getDamager() {
        return damager;
    }

    @Override public HandlerList getHandlers() {
        return handlerList;
    }
}
