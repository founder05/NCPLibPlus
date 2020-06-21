package me.marc_val_96.npclib.enums;

public enum NPCAnimation {
    /**
     * Makes npc swing his arm.
     */
    SWING_ARM(0),
    /**
     * Highlights the npc in red to mark that it has been damaged.
     */
    DAMAGE(1),
    /**
     * Moves npc's arm towards his mouth to eat food.
     */
    EAT_FOOD(3),
    /**
     * Displays criticial hit
     */
    CRITICAL_HIT(4),
    /**
     * Display magic critical hit
     */
    MAGIC_CRITICAL_HIT(5),
    /**
     * Makes npc crouch.
     */
    CROUCH(104),

    /**
     * Displays fire on npc.
     */
    ON_FIRE( 6),


    INVISIBLE( 32),

    /**
     * Stops npc from crouching.
     */

    UNCROUCH(105),


    LEAVE_BED(2);


    private final int id;

    private NPCAnimation(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
