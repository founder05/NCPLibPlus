package me.marc_val_96.npclib.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MessageManager {

    private static final MessageManager instance = new MessageManager();
    private final String prefix = ChatColor.GREEN + "[NPCLibPLus] ";

    private MessageManager() {
    }

    public static MessageManager getInstance() {
        return instance;
    }

    public void inform(CommandSender s, String msg) {
        msg(s, msg);
    }

    private void msg(CommandSender s, String msg) {
        s.sendMessage(prefix + ChatColor.WHITE + msg);
    }

}
