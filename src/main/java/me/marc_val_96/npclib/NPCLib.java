package me.marc_val_96.npclib;

import me.marc_val_96.npclib.enums.EquipmentSlot;
import me.marc_val_96.npclib.enums.NPCAnimation;
import me.marc_val_96.npclib.skin.NPCSkin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NPCLib {


    void delete();

    Location getLocation();

    int getEntityID(Player p);

    boolean isDeleted();

    int getNpcID();

    NPCSkin getSkin();

    /**
     * Get an org.bukkit.entity.Player instance from the npc.
     *
     * @return NPC's player instance.
     */
    Player getBukkitEntity();

    /**
     * Check whether or not an npc can get damaged
     *
     * @return NPC can be damaged?
     */
    boolean isGodmode();

    /**
     * Set whether or not an npc can get damaged.
     *
     * @param invulnerable NPC can be damaged?
     */
    void setGodmode(boolean invulnerable);

    /**
     * Check whether or not an npc has gravity enabled.
     *
     * @return NPC had gravity?
     */
    boolean isGravity();

    /**
     * Set whether or not an npc has gravity enabled.
     *
     * @param gravity NPC has gravity
     */
    void setGravity(boolean gravity);

    /**
     * Sets the player lying down or standing up.
     * Proper use of NPCAnimation.LEAVE_BED;
     *
     * @param x the body's x co-ordinate
     *          (Set to null to make entity stand up)
     * @param y the body's y co-ordinate
     *          (Set to null to make entity stand up)
     * @param z the body's z co-ordinate
     *          (Set to null to make entity stand up)
     *          <p>
     *          Will only make NPC stand up if all
     *          3 co-ordinates are set to null and
     *          NPC is lying down
     */
    @Deprecated void setLying(double x, double y, double z);

    /**
     * Checks whether the entity is lying down.
     *
     * @return Boolean value of whether the entity is lying down
     */
    @Deprecated boolean isLying();

    /**
     * Walk to a location.
     *
     * @param location Location to walk to
     * @return True if path created and applied correctly
     */
    boolean pathfindTo(Location location);

    /**
     * Walk to a location with custom speed
     *
     * @param location Location to walk to
     * @param speed    Speed to walk with (max 1.0, 0.2 default)
     * @return True if path created and applied correctly
     */
    boolean pathfindTo(Location location, double speed);

    /**
     * Walk to a location with custom speed and range.
     *
     * @param location Location to walk to
     * @param speed    Speed to walk with (max 1.0, 0.2 default)
     * @param range    Block radius limit for path finding (30 default)
     * @return True if path created and applied correctly
     */
    boolean pathfindTo(Location location, double speed, double range);

    /**
     * Get the entity the npc is looking at.
     *
     * @return Entity npc is looking at (null if not found)
     */
    Entity getTarget();

    /**
     * Make NPC look at an entity.
     *
     * @param target Entity to look at
     */
    void setTarget(Entity target);

    /**
     * Make npc look at a certain location.
     *
     * @param location Location to look at
     */
    void lookAt(Location location);

    /**
     * Change npc's yaw the proper way.
     *
     * @param yaw New npc yaw
     */
    void setYaw(float yaw);

    /**
     * Play an enums on the npc.
     *
     * @param animation Animation type to display
     */
    void playAnimation(NPCAnimation animation);

    /**
     * Set npc equipment.
     *
     * @param slot Slot type for the equipment
     * @param item Item to put on slot
     */
    void setEquipment(EquipmentSlot slot, ItemStack item);

    /**
     * == !!DEPRECATED!! ==
     * Use {@link #isCollisionEnabled() isCollisionEnabled} method instead.
     */
    @Deprecated boolean getEntityCollision();

    /**
     * == !!DEPRECATED!! ==
     * Use {@link #setCollisionEnabled(boolean) setCollisionEnabled} method instead.
     */
    @Deprecated void setEntityCollision(boolean entityCollision);

    /**
     * Get whether or not collision is enabled.
     *
     * @return Whether or not collision with entities is enabled
     */
    boolean isCollisionEnabled();

    /**
     * Set whether or not collision is enabled.
     *
     * @param collisionEnabled Whether or not collision with entities is enabled
     */
    void setCollisionEnabled(boolean collisionEnabled);
}
