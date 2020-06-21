package me.marc_val_96.npclib.skin;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NPCSkin {

    private final SkinType type;
    private final Plugin plugin;
    private String identifier;

    public NPCSkin(Plugin plugin, SkinType type, String identifier) {
        this.type = type;
        this.identifier = identifier;
        this.plugin = plugin;
    }

    public NPCSkin(Plugin plugin, SkinType type) {
        this.type = type;
        this.plugin = plugin;
    }

    public SkinType getSkinType() {
        return type;
    }

    public void getSkinDataAsync(SkinDataReply skinreply) {
        if (type == SkinType.IDENTIFIER) {
            SkinManager.getSkinFromMojangAsync(plugin, this.identifier, new SkinDataReply() {
                @Override public void done(SkinData skinData) {
                    if (skinData != null) {
                        skinreply.done(skinData);
                    } else {
                        SkinManager.getSkinFromMCAPIAsync(plugin, identifier, new SkinDataReply() {
                            @Override public void done(SkinData skinData) {
                                skinreply.done(skinData);
                            }
                        });
                    }
                }
            });
        } else if (type == SkinType.MINESKINID) {
            SkinManager.getSkinFromMineskinAsync(plugin, Integer.parseInt(this.identifier),
                new SkinDataReply() {
                    @Override public void done(SkinData skinData) {
                        skinreply.done(skinData);
                    }
                });
        }
    }

    public void getSkinDataAsync(SkinDataReply skinreply, Player p) {
        if (type == SkinType.PLAYER) {
            SkinManager
                .getSkinFromMojangAsync(plugin, p.getUniqueId().toString(), new SkinDataReply() {
                    @Override public void done(SkinData skinData) {
                        if (skinData != null) {
                            skinreply.done(skinData);
                        } else {
                            SkinManager
                                .getSkinFromMCAPIAsync(plugin, p.getName(), new SkinDataReply() {
                                    @Override public void done(SkinData skinData) {
                                        skinreply.done(skinData);
                                    }
                                });
                        }
                    }
                });
        }
    }
}
