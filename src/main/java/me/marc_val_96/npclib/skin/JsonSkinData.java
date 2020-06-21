package me.marc_val_96.npclib.skin;

public class JsonSkinData {

    private final SkinData skindata;
    private final long updated;

    public JsonSkinData(SkinData skindata, long updated) {
        this.skindata = skindata;
        this.updated = updated;
    }

    public SkinData getSkinData() {
        return skindata;
    }

    public long getTimeUpdated() {
        return updated;
    }
}


