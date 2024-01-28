package de.themarcraft.tmczfriends.commands;

import de.themarcraft.tmczfriends.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrivateMessage extends Command implements TabExecutor {

    Main plugin;

    public PrivateMessage(Main plugin) {
        super("msg", "themarcraft.friends.msg", "dm", "pm", "message", "tell", "tmcz-friends:dm", "tmcz-friends:pm", "tmcz-friends:message", "tmcz-friends:tell", "tmcz-friends:msg");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            plugin.log(plugin.getPlayerOnly());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (args.length == 0) {
            plugin.playerSendFriendMessage(player, "&cBitte gebe einen Spieler an");
        } else if (args.length == 1) {
            plugin.playerSendFriendMessage(player, "&cBitte gebe eine Nachricht an");
        } else {
            try {
                ProxiedPlayer reciver = plugin.getProxy().getPlayer(args[0]);
                if (plugin.database.getMessageSetting(reciver.getDisplayName()) == 0) {
                    plugin.playerSendFriendMessage(player, "Dieser Spieler empfängt keine Nachrichten");
                } else if (plugin.database.getMessageSetting(reciver.getDisplayName()) == 1) {
                    if (plugin.friend.isFriend(player.getDisplayName(), reciver.getDisplayName())) {
                        plugin.friend.sendMsg(player, plugin.getProxy().getPlayer(args[0]), String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                    } else {
                        plugin.friend.plugin.playerSendFriendMessage(player, "Dieser Spieler empfängt nur Nachrichten von Freunden");
                    }
                } else {
                    plugin.friend.sendMsg(player, plugin.getProxy().getPlayer(args[0]), String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                }
            } catch (Exception e) {
                plugin.playerSendFriendMessage(player, "&cDer Spieler ist Offline");
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        List<String> result = new ArrayList<>();
        if (args.length == 1) {
            for (ProxiedPlayer name : plugin.getProxy().getPlayers()) {
                if (name.getDisplayName().contains(args[0]) && name.getDisplayName() != ((ProxiedPlayer) commandSender).getDisplayName()) {
                    result.add(name.getDisplayName());
                }
            }
        }
        return result;
    }
}
