package de.themarcraft.tmczfriends;

import com.google.gson.JsonArray;
import de.themarcraft.tmczfriends.commands.Friend;
import de.themarcraft.tmczfriends.commands.PrivateMessage;
import de.themarcraft.tmczfriends.commands.Reply;
import de.themarcraft.tmczfriends.commands.ReplySettings;
import de.themarcraft.tmczfriends.listener.Database;
import de.themarcraft.tmczfriends.listener.PlayerListener;
import de.themarcraft.tmczfriends.listener.PluginMessanger;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
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

    public PluginMessanger pluginMessanger = new PluginMessanger(this);
    public Configuration configuration;

    public static String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // Setzen der Request-Methode auf GET
            connection.setRequestMethod("GET");

            // Lesen der Antwort
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();

            return response.toString();
        } finally {
            // Schließen der Verbindung
            connection.disconnect();
        }
    }

    @Override
    public void onEnable() {
        try {
            String result = sendGetRequest("http://tmcz.grasshopper-design.de/plugins/tmcz-friends/");
            if (!result.contains("\"tmcz-friends\" : true")) {
                throw new RuntimeException("Fehler bei der Verifizierung");
            } else {
                beforeLog("Plugin wird gestartet");
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler bei der Verifizierung");
        }

        try {
            makeConfig();
        } catch (IOException e) {
            log(e.getMessage());
        }
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
            if (configuration.get("config.addon.gui") == null) {
                configuration.set("config.addon.gui", false);
            }
            if (configuration.get("config.prefix.friends") == null) {
                configuration.set("config.prefix.friends", "&b&lFREUNDE &8» &r");
            }
            if (configuration.get("config.prefix.log") == null) {
                configuration.set("config.prefix.log", "&a&lTheMarCraft.de &7» &r");
            }
            if (configuration.get("config.database.host") == null) {
                configuration.set("config.database.host", "127.0.0.1");
            }
            if (configuration.get("config.database.database") == null) {
                configuration.set("config.database.database", "tmcz-friends");
            }
            if (configuration.get("config.database.user") == null) {
                configuration.set("config.database.user", "root");
            }
            if (configuration.get("config.database.passwd") == null) {
                configuration.set("config.database.passwd", "strongPassword##");
            }
            if (configuration.get("config.messages.playerOnly") == null) {
                configuration.set("config.messages.playerOnly", "&cDieser Befehl kann nur von einem Spieler ausgeführt werden");
            }
            if (configuration.get("config.messages.invalidPlayer") == null) {
                configuration.set("config.messages.invalidPlayer", "&cBitte gebe einen gültigen Spielernamen an");
            }
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        database = new Database(this);
        try {
            int i = 0;
            StringBuilder passwd = new StringBuilder();
            while (i < configuration.getString("config.database.passwd").length()) {
                passwd.append("*");
                i++;
            }
            log(ChatColor.RED + "Connecting to Database...");
            log(ChatColor.YELLOW + "Host: " + ChatColor.AQUA + configuration.getString("config.database.host"));
            log(ChatColor.YELLOW + "Database: " + ChatColor.AQUA + configuration.getString("config.database.database"));
            log(ChatColor.YELLOW + "User: " + ChatColor.AQUA + configuration.getString("config.database.user"));
            log(ChatColor.YELLOW + "Password: " + ChatColor.AQUA + passwd);

            database.initializeDatabase();

            log(ChatColor.GREEN + "Connected to Database: " + ChatColor.AQUA + configuration.getString("config.database.database"));
        } catch (SQLException e) {
            log(ChatColor.RED + "Error while Connecting to Database");
            log(ChatColor.RED + "Please Confirm Your MySQL Login Data and the MySQL Server");
            throw new RuntimeException(e);
        }

        getProxy().getPluginManager().registerCommand(this, new PrivateMessage(this));
        getProxy().getPluginManager().registerCommand(this, new Reply(this));
        //getProxy().getPluginManager().registerCommand(this, new ReplySettings(this));
        getProxy().getPluginManager().registerCommand(this, friend);

        getProxy().getPluginManager().registerListener(this, new PlayerListener(this));
        getProxy().getPluginManager().registerListener(this, pluginMessanger);

        getProxy().registerChannel("tmcz:friends");
        getProxy().registerChannel("tmcz:friendsbungee");

        log("");
        log("Plugin &b" + getDescription().getName() + ChatColor.GOLD + " " + getDescription().getVersion() + "&r loaded");
        log("");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void log(String msg) {
        getLogger().info(getPrefix() + ChatColor.translateAlternateColorCodes('&', msg));
    }

    public void beforeLog(String msg) {
        getLogger().info("TMCZ-Friends > " + ChatColor.translateAlternateColorCodes('&', msg));
    }

    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', configuration.getString("config.prefix.log"));
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
        return ChatColor.translateAlternateColorCodes('&', configuration.getString("config.prefix.friends"));
    }

    public String formatFriendsChat(String player1, String player2, String message) {
        String msg = getFriendsPrefix() + "&7[&c" + player1 + "&7 -> &c" + player2 + "&7] &r" + message;
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public void makeConfig() throws IOException {
        // Create plugin config folder if it doesn't exist
        if (!getDataFolder().exists()) {
            beforeLog("Config-Ordner " + ChatColor.AQUA + getDataFolder().mkdir() + ChatColor.RESET + " wird erstellt");
        }

        File configFile = new File(getDataFolder(), "config.yml");

        // Copy default config if it doesn't exist
        if (!configFile.exists()) {
            FileOutputStream outputStream = new FileOutputStream(configFile); // Throws IOException
            InputStream in = getResourceAsStream("config.yml"); // This file must exist in the jar resources folder
            in.transferTo(outputStream); // Throws IOException
        }
    }
}
