package de.themarcraft.tmczfriends.commands;

import de.themarcraft.tmczfriends.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Reply extends Command {

    Main plugin;

    public Reply(Main plugin) {
        super("reply", "themarcraft.friends.reply", "r", "rp", "antworten", "a");
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
                        PreparedStatement save = plugin.database.getConnection().prepareStatement("INSERT INTO `tmczFriendsMessages` (`id`, `sender`, `reciver`, `msg`) VALUES (NULL, ?, ?, ?);");
                        save.setString(1, player.getDisplayName());
                        save.setString(2, reciver.getDisplayName());
                        save.setString(3, message);
                        save.executeUpdate();
                        save.close();
                        player.sendMessage(plugin.formatFriendsChat("Du", reciver.getDisplayName(), message));
                        reciver.sendMessage(plugin.formatFriendsChat(player.getDisplayName(), "Dir", message));
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
