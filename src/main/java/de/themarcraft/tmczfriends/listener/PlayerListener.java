package de.themarcraft.tmczfriends.listener;

import de.themarcraft.tmczfriends.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerListener implements Listener {

    Main plugin;

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        try {
            PreparedStatement save = plugin.database.getConnection().prepareStatement("INSERT INTO `tmczFriendsFriends` (`player`) VALUES (?);");
            save.setString(1, player.getDisplayName());
            save.executeUpdate();
            save.close();

            if (plugin.friend.getRequestsInt(player.getDisplayName()) != 0) {
                plugin.playerSendFriendMessage(player, "Du hast " + plugin.friend.getRequestsInt(player.getDisplayName()) + " offene Freundschafts-Anfragen");
            }

            PreparedStatement save2 = plugin.database.getConnection().prepareStatement("INSERT INTO `tmczFriendsSettings` (`player`, `friendSwitch`, `friendJoin`, `replyType`, `msg`) VALUES (?, ?, ?, ?);");
            save2.setString(1, player.getDisplayName());
            save2.setInt(2, 0);
            save2.setInt(3, 0);
            save2.setInt(4, 0);
            save2.setInt(5, 2);
            save2.executeUpdate();
            save2.close();
        } catch (SQLException e) {
            plugin.log(e.getMessage());
        }
        try {
            String[] freunde = plugin.friend.getFriends(player.getDisplayName()).split(",");
            for (String s : freunde) {
                if (plugin.database.getFriendJoinSetting(s)) {
                    try {
                        plugin.playerSendFriendMessage(plugin.getProxy().getPlayer(s), ChatColor.AQUA + player.getDisplayName() + ChatColor.GRAY + " ist nun §aOnline");
                    } catch (Exception e) {
                        plugin.log(e.getMessage());
                    }
                }
                plugin.log(plugin.database.getFriendJoinSetting(s) + "");
                plugin.log(s);
            }
        } catch (Exception e) {
            plugin.log(e.getMessage());
        }
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {
        ProxiedPlayer player = event.getPlayer();
        try {
            String[] freunde = plugin.friend.getFriends(player.getDisplayName()).split(",");
            for (String s : freunde) {
                if (plugin.database.getFriendSwitchSetting(s)) {
                    try {
                        plugin.playerSendFriendMessage(plugin.getProxy().getPlayer(s), ChatColor.AQUA + player.getDisplayName() + ChatColor.GRAY + " spielt nun auf " + ChatColor.AQUA + player.getServer().getInfo().getName());
                    } catch (Exception e) {
                        //plugin.log(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            //plugin.log(e.getMessage());
        }
    }

    @EventHandler
    public void onLeaveProxy(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        try {
            String[] freunde = plugin.friend.getFriends(player.getDisplayName()).split(",");
            for (String s : freunde) {
                if (plugin.database.getFriendJoinSetting(s)) {
                    try {
                        plugin.playerSendFriendMessage(plugin.getProxy().getPlayer(s), ChatColor.AQUA + player.getDisplayName() + ChatColor.GRAY + " ist nun §cOffline");
                    } catch (Exception e) {
                        plugin.log(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            //plugin.log(e.getMessage());
        }
    }
}
