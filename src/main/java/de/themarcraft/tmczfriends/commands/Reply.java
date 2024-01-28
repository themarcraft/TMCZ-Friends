package de.themarcraft.tmczfriends.commands;

import de.themarcraft.tmczfriends.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Reply extends Command {

    Main plugin;

    public Reply(Main plugin) {
        super("reply", "themarcraft.friends.reply", "r", "rp", "antworten", "a", "tmcz-friends:r", "tmcz-friends:rp", "tmcz-friends:antworten", "tmcz-friends:a", "tmcz-friends:reply");
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
            plugin.playerSendFriendMessage(player, "&cBitte gebe eine Nachricht an");
            return;
        } else {
            try {
                PreparedStatement statement;
                //plugin.log(String.valueOf(plugin.database.getReplyType(player.getDisplayName())));
                if (plugin.database.getReplyType(player.getDisplayName())) {
                    statement = plugin.database.getConnection().prepareStatement("SELECT * FROM tmczFriendsMessages WHERE reciver = ? ORDER BY id DESC LIMIT 1;");
                    //player.sendMessage("An meine letzte empfangene Nachricht");
                } else {
                    statement = plugin.database.getConnection().prepareStatement("SELECT * FROM tmczFriendsMessages WHERE sender = ? ORDER BY id DESC LIMIT 1;");
                    //player.sendMessage("An meine letzte gesendete Nachricht");
                }
                statement.setString(1, player.getDisplayName());
                ResultSet resultSet = statement.executeQuery();
                String message = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
                if (resultSet.next()) {
                    try {
                        ProxiedPlayer reciver;
                        if (plugin.database.getReplyType(player.getDisplayName())) {
                            reciver = plugin.getProxy().getPlayer(resultSet.getString("sender"));
                        } else {
                            reciver = plugin.getProxy().getPlayer(resultSet.getString("reciver"));
                        }
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
                statement.close();
            } catch (SQLException e) {
                plugin.playerSendFriendMessage(player, "&cBitte gebe eine Nachricht an");
            }

        }
    }
}
