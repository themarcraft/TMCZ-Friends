package de.themarcraft.tmczfriends;

import de.themarcraft.tmczfriends.commands.Friend;
import de.themarcraft.tmczfriends.commands.PrivateMessage;
import de.themarcraft.tmczfriends.commands.Reply;
import de.themarcraft.tmczfriends.commands.ReplySettings;
import de.themarcraft.tmczfriends.listener.Database;
import de.themarcraft.tmczfriends.listener.PlayerListener;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.SQLException;

/**
 * Main Klasse des Plugins. Dies Enth&auml;lt viele Funktionen, die man h&auml;ufiger mal braucht und registriert auch Befehle und Listener
 *
 * @author Marvin Niermann
 * @version 1.0-Snapshot
 */

public final class Main extends Plugin {

    public Database database;

    public Friend friend = new Friend(this);

    @Override
    public void onEnable() {

        database = new Database(this);
        try {
            database.initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        log("");
        log("Plugin &b" + getDescription().getName() + ChatColor.GOLD + " " + getDescription().getVersion() + "&r loaded");
        log("");

        getProxy().getPluginManager().registerCommand(this, new PrivateMessage(this));
        getProxy().getPluginManager().registerCommand(this, new Reply(this));
        getProxy().getPluginManager().registerCommand(this, new ReplySettings(this));
        getProxy().getPluginManager().registerCommand(this, friend);

        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));

        getProxy().registerChannel("tmcz:friends");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void log(String msg) {
        getLogger().info(getPrefix() + ChatColor.translateAlternateColorCodes('&', msg));
    }

    public void beforeLog(String msg) {
        getLogger().info("TMCZ-Bungee > " + ChatColor.translateAlternateColorCodes('&', msg));
    }

    public String getPrefix() {
        return database.getPrefix();
    }

    public String getPlayerOnly() {
        return database.getPlayerOnly();
    }

    public void playerSendMessage(ProxiedPlayer player, String message) {
        player.sendMessage(getPrefix() + ChatColor.translateAlternateColorCodes('&', message));
    }

    public void playerSendFriendMessage(ProxiedPlayer player, String message) {
        player.sendMessage(getFriendsPrefix() + ChatColor.translateAlternateColorCodes('&', "&7" + message));
    }

    public String getFriendsPrefix() {
        return ChatColor.translateAlternateColorCodes('&', "&b&lFREUNDE &8» &r");
    }

    public String formatFriendsChat(String player1, String player2, String message) {
        String msg = getFriendsPrefix() + "&7[&c" + player1 + "&7 -> &c" + player2 + "&7] &r" + message;
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
