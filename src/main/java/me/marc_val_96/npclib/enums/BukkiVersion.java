package me.marc_val_96.npclib.enums;

public enum BukkiVersion {


    V1_8_R2, V1_8_R3, V1_9_R1, V1_9_R2, V1_10_R1, V1_11_R1, V1_12_R1, V1_13_R1, V1_13_R2, V1_14_R1, V1_15_R1;

    public boolean isAboveOrEqual(BukkiVersion compare) {
        return ordinal() >= compare.ordinal();
    }
}
