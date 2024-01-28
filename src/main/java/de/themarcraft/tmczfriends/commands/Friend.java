package de.themarcraft.tmczfriends.commands;

import de.themarcraft.tmczfriends.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Freunde und eine gro&szlig;e Anzahl an weiteren Funktionen
 * Hinzuf&uuml;gen- und Entfernen- von Freunden, Befehl, TabCompleter, etc.
 */

public class Friend extends Command implements TabExecutor {

    Main plugin;

    public Friend(Main plugin) {
        super("friends", "themarcraft.friends", "freunde", "tmcz-friends:freunde", "tmcz-friends:friends");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if (commandSender == ProxyServer.getInstance().getConsole()) {
            plugin.log(plugin.getPlayerOnly());
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        if (args.length == 0) {
            if (plugin.configuration.getBoolean("config.addon.gui")) {
                plugin.pluginMessanger.sendPluginMesssage("friendui", player.getDisplayName());
                return;
            }
            plugin.playerSendMessage(player, "§8§m･････････････････････････････････････････････････････････････････････････････････････････････････････");
            plugin.playerSendMessage(player, ChatColor.GRAY + plugin.getDescription().getName());
            plugin.playerSendMessage(player, "");
            plugin.playerSendMessage(player, ChatColor.GRAY + "/freunde hilfe");
            plugin.playerSendMessage(player, "");
            plugin.playerSendMessage(player, ChatColor.GRAY + "Version: " + ChatColor.AQUA + plugin.getDescription().getVersion());
            plugin.playerSendMessage(player, "§8§m･････････････････････････････････････････････････････････････････････････････････････････････････････");

            return;
        }
        switch (args[0]) {
            case "add":
            case "hinzufügen":
                if (args.length == 2) {
                    sendRequest(player.getDisplayName(), args[1]);
                } else {
                    plugin.playerSendFriendMessage(player, plugin.database.getInvalidPlayer());
                }
                break;
            case "refuse":
            case "ablehnen":
                if (args.length == 2) {
                    if (refuse(player.getDisplayName(), args[1])) {
                        plugin.playerSendFriendMessage(player, "Du hast erfolgreich die Freundschafts-Anfrage von " + ChatColor.AQUA + args[1] + ChatColor.GRAY + " abgelehnt");
                    } else {
                        plugin.playerSendFriendMessage(player, "Du hast von " + ChatColor.AQUA + args[1] + ChatColor.GRAY + " keine Freundschafts-Anfrage bekommen");
                    }
                } else {
                    plugin.playerSendFriendMessage(player, plugin.database.getInvalidPlayer());
                }
                break;
            case "requests":
            case "anfragen":
                if (plugin.configuration.getBoolean("config.addon.gui")) {
                    plugin.pluginMessanger.sendPluginMesssage("requestui", player.getDisplayName());
                    return;
                }
                String[] requests = getRequests(player.getDisplayName()).split(",");
                plugin.playerSendFriendMessage(player, "Offene Freundschafts-Anfragen:");
                for (String s : requests) {
                    plugin.playerSendFriendMessage(player, s);
                }
                break;
            case "accept":
            case "annehmen":
                if (args.length != 2) {
                    plugin.playerSendFriendMessage(player, plugin.database.getInvalidPlayer());
                    break;
                }
                if (isRequest(player.getDisplayName(), args[1]) == 1) {
                    refuse(player.getDisplayName(), args[1]);
                    add(player.getDisplayName(), args[1]);
                    plugin.playerSendFriendMessage(player, "Du bist nun mit " + ChatColor.AQUA + args[1] + ChatColor.GRAY + " befreundet");
                    if (plugin.getProxy().getPlayer(args[1]) != null) {
                        plugin.playerSendFriendMessage(plugin.getProxy().getPlayer(args[1]), ChatColor.AQUA + player.getDisplayName() + ChatColor.GRAY + " hat deine Freundschafts-Anfrage angenommen");
                    }
                } else {
                    plugin.playerSendFriendMessage(player, ChatColor.AQUA + args[1] + ChatColor.GRAY + " hat dir keine Freundschafts-Anfrage geschickt");
                }
                break;
            case "friends":
            case "freunde":
                if (plugin.configuration.getBoolean("config.addon.gui")) {
                    plugin.pluginMessanger.sendPluginMesssage("friendui", player.getDisplayName());
                    return;
                }
                plugin.playerSendFriendMessage(player, "Deine Freunde:");
                String[] freunde = getFriends(player.getDisplayName()).split(",");
                for (String s : freunde) {
                    plugin.playerSendFriendMessage(player, s);
                }
                break;
            case "remove":
            case "entfernen":
                if (args.length == 2) {
                    if (remove(player.getDisplayName(), args[1])) {
                        plugin.playerSendFriendMessage(player, ChatColor.AQUA + args[1] + ChatColor.GRAY + " wurde aus deiner Freundschaftsliste entfernt");
                    }
                } else {
                    plugin.playerSendFriendMessage(player, plugin.database.getInvalidPlayer());
                }
                break;
            case "hilfe":
            case "help":
                sendHelp(player);
                break;
            case "info":
                plugin.playerSendMessage(player, "§8§m･････････････････････････････････････････････････････････････････････････････････････････････････････");
                plugin.playerSendMessage(player, ChatColor.GRAY + plugin.getDescription().getName());
                plugin.playerSendMessage(player, "");
                plugin.playerSendMessage(player, ChatColor.GRAY + "/freunde hilfe");
                plugin.playerSendMessage(player, "");
                plugin.playerSendMessage(player, ChatColor.GRAY + "Version: " + ChatColor.AQUA + plugin.getDescription().getVersion());
                plugin.playerSendMessage(player, "§8§m･････････････････････････････････････････････････････････････････････････････････････････････････････");
                break;
            case "settings":
            case "einstellungen":
                if (plugin.configuration.getBoolean("config.addon.gui") && args.length == 1) {
                    plugin.pluginMessanger.sendPluginMesssage("settingsui", player.getDisplayName());
                    return;
                }
                if (args.length == 1) {
                    plugin.playerSendFriendMessage(player, "Verfügbare Einstellungen:");
                    plugin.playerSendFriendMessage(player, "/freunde einstellungen antworten");
                    plugin.playerSendFriendMessage(player, "/freunde einstellungen msg");
                    plugin.playerSendFriendMessage(player, "/freunde einstellungen freundeOnline");
                    plugin.playerSendFriendMessage(player, "/freunde einstellungen freundeServerWechsel");
                    return;
                }
                switch (args[1]) {
                    case "antworten":
                    case "reply":
                        if (args.length >= 3)
                            switch (args[2]) {
                                case "1":
                                    plugin.database.setReplyType(player.getDisplayName(), false);
                                    plugin.playerSendFriendMessage(player, "Du Sendest mit /r nun Nachrichten an die Person, der du zuletzt geschrieben hast");
                                    break;
                                case "2":
                                    plugin.database.setReplyType(player.getDisplayName(), true);
                                    plugin.playerSendFriendMessage(player, "Du Sendest mit /r nun Nachrichten an die Person, die dir zuletzt geschrieben hat");
                                    break;
                                default:
                                    plugin.playerSendFriendMessage(player, "Antworten auf Private Nachrichten (/r)");
                                    plugin.playerSendFriendMessage(player, "1 = Antworte auf einkommende Nachrichten");
                                    plugin.playerSendFriendMessage(player, "2 = Sende Nachricht an letzte Person, der du geschrieben hast");
                                    break;
                            }
                        else {
                            plugin.playerSendFriendMessage(player, "Antworten auf Private Nachrichten (/r)");
                            plugin.playerSendFriendMessage(player, "1 = Antworte auf einkommende Nachrichten");
                            plugin.playerSendFriendMessage(player, "2 = Sende Nachricht an letzte Person, der du geschrieben hast");
                        }
                        break;
                    case "msg":
                    case "nachrichten":
                    case "dm":
                        if (args.length >= 3)
                            switch (args[2]) {
                                case "aus":
                                    plugin.database.setMessageSetting(player.getDisplayName(), 0);
                                    plugin.playerSendFriendMessage(player, "Du kriegst nun keine Privaten Nachrichten mehr");
                                    break;
                                case "freunde":
                                    plugin.database.setMessageSetting(player.getDisplayName(), 1);
                                    plugin.playerSendFriendMessage(player, "Nur Freunde können dir jetzt Private Nachrichten senden");
                                    break;
                                case "alle":
                                    plugin.database.setMessageSetting(player.getDisplayName(), 2);
                                    plugin.playerSendFriendMessage(player, "Jeder kann dir jetzt private Nachrichten senden");
                                    break;
                                default:
                                    plugin.playerSendFriendMessage(player, "Private Nachrichten (/msg)");
                                    plugin.playerSendFriendMessage(player, "aus = Keiner kann dir Private Nachrichten schicken");
                                    plugin.playerSendFriendMessage(player, "freunde = Nur Freunde können dir Private Nachrichten schicken");
                                    plugin.playerSendFriendMessage(player, "alle = Jeder kann dir Private Nachrichten schicken");
                                    break;
                            }
                        else {
                            plugin.playerSendFriendMessage(player, "Private Nachrichten (/msg)");
                            plugin.playerSendFriendMessage(player, "aus = Keiner kann dir Private Nachrichten schicken");
                            plugin.playerSendFriendMessage(player, "freunde = Nur Freunde können dir Private Nachrichten schicken");
                            plugin.playerSendFriendMessage(player, "alle = Jeder kann dir Private Nachrichten schicken");
                        }
                        break;
                    case "freundeOnline":
                    case "friendsOnline":
                    case "online":
                        if (args.length >= 3)
                            switch (args[2]) {
                                case "aus":
                                    plugin.database.setFriendJoinSetting(player.getDisplayName(), false);
                                    plugin.playerSendFriendMessage(player, "Du kriegst nun keine Benachrichtigung mehr, wenn deine Freunde Online sind");
                                    break;
                                case "an":
                                    plugin.database.setFriendJoinSetting(player.getDisplayName(), true);
                                    plugin.playerSendFriendMessage(player, "Du kriegst nun Benachrichtigungen, wenn deine Freunde Online sind");
                                    break;
                                default:
                                    plugin.playerSendFriendMessage(player, "Benachrichtigung - Wenn deine Freunde Online gehen");
                                    plugin.playerSendFriendMessage(player, "aus = Du kriegst Benachrichtigungen, wenn deine Freunde Online gehen");
                                    plugin.playerSendFriendMessage(player, "an = Du kriegst keine Benachrichtigungen, wenn deine Freunde Online gehen");
                                    break;
                            }
                        else {
                            plugin.playerSendFriendMessage(player, "Benachrichtigung - Wenn deine Freunde Online gehen");
                            plugin.playerSendFriendMessage(player, "aus = Du kriegst Benachrichtigungen, wenn deine Freunde Online gehen");
                            plugin.playerSendFriendMessage(player, "an = Du kriegst keine Benachrichtigungen, wenn deine Freunde Online gehen");
                        }
                        break;
                    case "freundeServerWechsel":
                    case "serverwechsel":
                    case "switch":
                    case "serverSwitch":
                        if (args.length >= 3)
                            switch (args[2]) {
                                case "aus":
                                    plugin.database.setFriendSwitchSetting(player.getDisplayName(), false);
                                    plugin.playerSendFriendMessage(player, "Du kriegst nun keine Benachrichtigung mehr, wenn deine Freunde die Server wechseln");
                                    break;
                                case "an":
                                    plugin.database.setFriendSwitchSetting(player.getDisplayName(), true);
                                    plugin.playerSendFriendMessage(player, "Du kriegst nun Benachrichtigungen, wenn deine Freunde die Server wechseln");
                                    break;
                                default:
                                    plugin.playerSendFriendMessage(player, "Benachrichtigung - Wenn deine Freunde Online gehen");
                                    plugin.playerSendFriendMessage(player, "aus = Du kriegst Benachrichtigungen, wenn deine Freunde die Server wechseln");
                                    plugin.playerSendFriendMessage(player, "an = Du kriegst keine Benachrichtigungen, wenn deine Freunde die Server wechseln");
                                    break;
                            }
                        else {
                            plugin.playerSendFriendMessage(player, "Benachrichtigung - Wenn deine Freunde Online gehen");
                            plugin.playerSendFriendMessage(player, "aus = Du kriegst Benachrichtigungen, wenn deine Freunde die Server wechseln");
                            plugin.playerSendFriendMessage(player, "an = Du kriegst keine Benachrichtigungen, wenn deine Freunde die Server wechseln");
                        }
                        break;
                    default:
                        plugin.playerSendFriendMessage(player, "Verfügbare Einstellungen:");
                        plugin.playerSendFriendMessage(player, "/freunde einstellungen antworten");
                        plugin.playerSendFriendMessage(player, "/freunde einstellungen msg");
                        plugin.playerSendFriendMessage(player, "/freunde einstellungen freundeOnline");
                        plugin.playerSendFriendMessage(player, "/freunde einstellungen freundeServerWechsel");
                }
                break;
            default:
                plugin.playerSendFriendMessage(player, "§cDiesen Befehl gibt es nicht");
        }
    }

    /**
     * Hilfemen&uuml;
     *
     * @param player wird das Hilfemenü gesendet
     */

    public void sendHelp(ProxiedPlayer player) {
        plugin.playerSendFriendMessage(player, "Folgende Befehle kannst du verwenden:");
        plugin.playerSendFriendMessage(player, "/freunde §8| §7Öffnet das Freunde Menü");
        plugin.playerSendFriendMessage(player, "/freunde anfragen §8| §7Zeigt offene Freundschafts-Anfragen");
        plugin.playerSendFriendMessage(player, "/freunde hinzufügen <name> §8| §7Sendet einem Spieler eine Freundschafts-Anfrage");
        plugin.playerSendFriendMessage(player, "/freunde entfernen <name> §8| §7Löst die Freundschaft mit einem Spieler auf");
        plugin.playerSendFriendMessage(player, "/freunde aktzeptieren <name> §8| §7Aktzeptiere die Freundschafts-Anfrage von dem Spieler");
        plugin.playerSendFriendMessage(player, "/freunde ablehnen <name> §8| §7Lehnt die Freundschafts-Anfrage eines Spielers ab");
        plugin.playerSendFriendMessage(player, "/freunde einstellungen §8| §7Ändere deine Einstellungen");
    }

    /**
     * Freundschafts-Anfrage senden
     *
     * @param player Spieler, der die Freundschafts-Anfrage sendet
     * @param name   Spieler, der die Freundschafts-Anfrage bekommt
     * @return false wenn es fehler gab, sonst true
     */

    public boolean sendRequest(String player, String name) {
        try {
            ProxiedPlayer send = plugin.getProxy().getPlayer(player);
            //plugin.log(isMax(player) + " Spieler: " + player);
            if (isMax(player)) {
                plugin.playerSendFriendMessage(send, "§cDu hast das Freunde Limit erreicht!");
                return false;
            }
            if (!isFriend(player, name)) {
                if (isRequest(name, player) == 0) {
                    PreparedStatement save = plugin.database.getConnection().prepareStatement("INSERT INTO `tmczFriendsRequests` (`player`, `reciver`) VALUES (?, ?);");
                    save.setString(1, player);
                    save.setString(2, name);
                    save.executeUpdate();
                    save.close();
                    plugin.playerSendFriendMessage(send, "Du hast erfolgreich eine Freundschafts-Anfrage an " + ChatColor.AQUA + name + ChatColor.GRAY + " gesendet");
                    if (plugin.getProxy().getPlayer(name) != null) {
                        plugin.playerSendFriendMessage(plugin.getProxy().getPlayer(name), ChatColor.GRAY + "Du hast eine Freundschafts-Anfrage von " + ChatColor.AQUA + player + ChatColor.GRAY + " bekommen");
                        //plugin.playerSendFriendMessage(plugin.getProxy().getPlayer(name), ChatColor.GRAY + "Mache " + ChatColor.AQUA + "/freunde annehmen " + ChatColor.GRAY + "um die Anfrage anzunehmen");

                        TextComponent accept = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "Annehmen" + ChatColor.DARK_GRAY + "]");
                        List hoverAccept = new ArrayList<>();
                        hoverAccept.add(ChatColor.GREEN + "Freundschafts-Anfrage Annehmen");
                        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverAccept));
                        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/freunde annehmen " + player));

                        TextComponent decline = new TextComponent(ChatColor.DARK_GRAY + "[" + ChatColor.RED + "Ablehnen" + ChatColor.DARK_GRAY + "]");
                        List hoverDecline = new ArrayList<>();
                        hoverDecline.add(ChatColor.RED + "Freundschafts-Anfrage Ablehnen");
                        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverDecline));
                        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/freunde ablehnen " + player));

                        TextComponent result = new TextComponent();
                        result.addExtra(plugin.getFriendsPrefix());
                        result.addExtra(accept);
                        result.addExtra(" ");
                        result.addExtra(decline);
                        plugin.getProxy().getPlayer(name).sendMessage(ChatMessageType.CHAT, result);

                    }
                    return true;
                } else if (isRequest(name, player) == 2) {
                    plugin.playerSendFriendMessage(send, "Du hast bereits eine Freundschafts-Anfrage von " + ChatColor.AQUA + name + ChatColor.GRAY + " bekommen");
                    plugin.playerSendFriendMessage(send, "Freundschafts-Anfrage von " + ChatColor.AQUA + name + ChatColor.GRAY + " angenommen");
                    add(player, name);
                    return true;
                } else {
                    plugin.playerSendFriendMessage(send, "Du kannst nur einmal eine Freundschafts-Anfrage an die selbe person schicken");
                    return false;
                }
            } else {
                plugin.playerSendFriendMessage(send, "Der Spieler ist bereits dein Freund");
                return false;
            }
        } catch (SQLException e) {
            plugin.log("E1: " + e.getMessage());
            return false;
        }
    }

    /**
     * Hinzuf&uuml;gen eines Freundes
     *
     * @param player Spieler, dem ein Freund hinzugef&uuml;gt wird
     * @param name   Hinzugefügter Freund
     * @return false wenn es fehler gab, sonst true
     */

    public boolean add(String player, String name) {
        try {
            if (!isMax(player)) {
                PreparedStatement add = plugin.database.getConnection().prepareStatement("UPDATE `tmczFriendsFriends` SET `friends` = ? WHERE `tmczFriendsFriends`.`player` = ?;");
                add.setString(1, getFriends(player) + name + ",");
                add.setString(2, player);
                add.executeUpdate();
                add.close();
                if (!isFriend(name, player)) {
                    try {
                        refuse(player, name);
                        refuse(name, player);
                    } catch (Exception e) {

                    }
                    add(name, player);
                }

                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            plugin.log("Exeption: " + e.getMessage());
            return false;
        }

    }

    /**
     * Freundschafts-Anfrage l&ouml;schen
     *
     * @param player Spieler, der die Anfrage gesendet hat
     * @param name   Spieler, der die Anfrage bekommen würde
     * @return false wenn es fehler gab, sonst true
     */
    public boolean refuse(String player, String name) {
        try {
            if (isRequest(player, name) == 1) {
                PreparedStatement delete = plugin.database.getConnection().prepareStatement("DELETE FROM tmczFriendsRequests WHERE `player` = ? AND `reciver` = ?;");
                delete.setString(2, player);
                delete.setString(1, name);
                delete.executeUpdate();
                delete.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            plugin.log("Exeption: " + e.getMessage());
            return false;
        }
    }

    /**
     * Freunde von einem Spieler bekommen
     *
     * @param player Spieler, der abgefragt wird
     * @return Liste der Freunde des Spielers, geteilt durch ',' komma
     */
    public String getFriends(String player) {
        try {
            PreparedStatement statement = plugin.database.getConnection().prepareStatement("SELECT * FROM tmczFriendsFriends WHERE player = ?;");
            statement.setString(1, player);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String result = resultSet.getString("friends");
            if (result != null) {
                result = result.replace("null", "").replace("NULL", "");
            } else {
                result = "";
            }
            statement.close();
            return result;
        } catch (SQLException e) {
            plugin.log(e.getMessage());
            return "";
        }
    }

    /**
     * Pr&uuml;ft ob der Spieler eine Freundschafts-Anfrage von einem Spieler bekommen hat
     *
     * @param player Spieler, der die Freundschafts-Anfrage bekommen haben soll
     * @param name   Spieler, der die Freundschafts-Anfrage gesendet haben soll
     * @return 0 = nein, 1 = ja, 2 = Beide Spieler haben sich gegenseitig eine Freundschafts-Anfrage gesendet
     */
    public int isRequest(String player, String name) {
        try {
            PreparedStatement statement = plugin.database.getConnection().prepareStatement("SELECT * FROM tmczFriendsRequests WHERE player = ? AND reciver = ?;");
            statement.setString(1, name);
            statement.setString(2, player);

            ResultSet resultSet = statement.executeQuery();

            PreparedStatement statement2 = plugin.database.getConnection().prepareStatement("SELECT * FROM tmczFriendsRequests WHERE player = ? AND reciver = ?;");
            statement2.setString(1, player);
            statement2.setString(2, name);

            ResultSet resultSet2 = statement2.executeQuery();

            boolean rs = resultSet.next();
            boolean rs2 = resultSet2.next();

            if (rs && rs2) {
                statement.close();
                statement2.close();
                return 2;
            } else if (rs || rs2) {
                statement.close();
                statement2.close();
                return 1;
            } else {
                statement.close();
                statement2.close();
                return 0;
            }
        } catch (Exception e) {
            plugin.log(e.toString());
            return 0;
        }
    }

    /**
     * &Uuml;berprüft, ob der Spieler einen bestimmten Freund hat
     *
     * @param player Spieler, der &uuml;berprüft wird
     * @param name   Spieler, der der Freund sein soll
     * @return false wenn es fehler gab, sonst true
     */
    public boolean isFriend(String player, String name) {
        String friendsList = getFriends(player);
        if (friendsList.contains(name)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Freundschafts-Anfragen eines Spielers
     *
     * @param player Freundschafts-Anfragen werden von dem Spieler abgefragt
     * @return Liste der Freundschafts-Anfragen des Spielers
     */
    public String getRequests(String player) {
        try {
            PreparedStatement statement = plugin.database.getConnection().prepareStatement("SELECT * FROM tmczFriendsRequests WHERE reciver = ?;");
            statement.setString(1, player);

            ResultSet resultSet = statement.executeQuery();
            String result = "";
            while (resultSet.next()) {
                result = result + resultSet.getString("player") + ",";
            }
            return result;
        } catch (SQLException e) {
            return "";
        }
    }

    /**
     * Freundschafts-Anfragen eines Spielers
     *
     * @param player Freundschafts-Anfragen werden von dem Spieler abgefragt
     * @return Zahl der offenen Freundschafts-Anfragen
     */
    public int getRequestsInt(String player) {
        try {
            PreparedStatement statement = plugin.database.getConnection().prepareStatement("SELECT * FROM tmczFriendsRequests WHERE reciver = ?;");
            statement.setString(1, player);

            ResultSet resultSet = statement.executeQuery();
            int result = 0;
            while (resultSet.next()) {
                result++;
            }
            //plugin.log(String.valueOf(result));
            return result;
        } catch (SQLException e) {
            //plugin.log(String.valueOf(0));
            //plugin.log(e.getMessage());
            return 0;
        }
    }

    /**
     * Entfernen eines Freundes
     *
     * @param player Spieler, dem der Freund entfernt wird
     * @param name   Freund, der entfernt wird
     * @return false wenn es fehler gab, sonst true
     */
    public boolean remove(String player, String name) {
        ProxiedPlayer send = plugin.getProxy().getPlayer(player);
        if (!isFriend(player, name)) {
            plugin.playerSendFriendMessage(send, ChatColor.AQUA + name + ChatColor.GRAY + " ist nicht dein Freund");
            return false;
        } else {
            try {
                String friends1 = getFriends(player).replace(name + ",", "");
                PreparedStatement remove1 = plugin.database.getConnection().prepareStatement("UPDATE `tmczFriendsFriends` SET `friends` = ? WHERE `tmczFriendsFriends`.`player` = ?;");
                remove1.setString(1, friends1);
                remove1.setString(2, player);
                remove1.executeUpdate();
                remove1.close();

                String friends2 = getFriends(name).replace(player + ",", "");
                PreparedStatement remove2 = plugin.database.getConnection().prepareStatement("UPDATE `tmczFriendsFriends` SET `friends` = ? WHERE `tmczFriendsFriends`.`player` = ?;");
                remove2.setString(1, friends2);
                remove2.setString(2, name);
                remove2.executeUpdate();
                remove2.close();
                return true;
            } catch (SQLException e) {
                plugin.log(e.getMessage());
                throw new RuntimeException(e);
                //return false;
            }
        }
    }

    /**
     * Schaut, ob der Spieler bereits das maximum an Freunden erreicht hat
     *
     * @param player Spieler, der &uuml;berpr&uuml;ft wird
     * @return true, wenn der spieler zu viele Freunde hat, sonst false
     */
    public boolean isMax(String player) {

        if (getFriends(player).split(",").length >= getMax(player)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Hole die maximale Anzahl eines Spielers
     *
     * @param player Spieler, der &uuml;berpr&uuml;ft wird
     * @return gibt den Wert anhand der Permission wieder
     */
    public int getMax(String player) {
        int max = 28;
        int i = 1008;
        while (i > 0) {
            try {
                if (plugin.getProxy().getPlayer(player).hasPermission("themarcraft.friends." + i)) {
                    max = i;
                    break;
                } else {
                    i--;
                }
            } catch (Exception e) {
                break;
            }
        }
        if (i == 1) {
            max = 20;
        }
        return max;
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) commandSender;
        List<String> result = new ArrayList<>();
        List<String> args1 = new ArrayList<>();
        List<String> settings = new ArrayList<>();

        try {
            args1.add("hinzufügen");
            args1.add("entfernen");
            args1.add("annehmen");
            args1.add("ablehnen");
            args1.add("anfragen");
            args1.add("freunde");
            args1.add("hilfe");
            args1.add("einstellungen");
            args1.add("info");
            settings.add("antworten");
            settings.add("msg");
            settings.add("freundeOnline");
            settings.add("freundeServerWechsel");

            if (args.length == 1) {
                if (args[0].isEmpty()) {
                    result = args1;
                } else {
                    for (Object c : args1) {
                        if (c.toString().contains(args[0])) {
                            result.add(c.toString());
                        }
                    }
                }
            } else if (args.length >= 2) {
                switch (args[0]) {
                    case "hilfe":
                    case "help":
                    case "freunde":
                    case "anfragen":
                        break;
                    case "hinzufügen":
                        for (ProxiedPlayer name : plugin.getProxy().getPlayers()) {
                            if (!isFriend(player.getDisplayName(), name.getDisplayName()) && name != player) {
                                if (name.getDisplayName().contains(args[1])) {
                                    result.add(name.getDisplayName());
                                }
                            }
                        }
                        break;
                    case "annehmen":
                    case "ablehnen":
                        String[] requests = getRequests(player.getDisplayName()).split(",");
                        for (String s : requests) {
                            result.add(s);
                        }
                        break;
                    case "entfernen":
                        String[] friends = getFriends(player.getDisplayName()).split(",");
                        for (String s : friends) {
                            result.add(s);
                        }
                        break;
                    case "einstellungen":
                    case "settings":
                        if (args.length == 2) {
                            for (String s : settings) {
                                if (s.contains(args[1])) {
                                    result.add(s);
                                }
                            }
                        }
                        if (args.length == 3) {
                            switch (args[1]) {
                                case "antworten":
                                    result.add("1");
                                    result.add("2");
                                    break;
                                case "msg":
                                    result.add("aus");
                                    result.add("freunde");
                                    result.add("alle");
                                    break;
                                case "freundeOnline":
                                case "freundeServerWechsel":
                                    result.add("an");
                                    result.add("aus");
                                    break;
                            }
                        }
                        break;
                }
            }
        } catch (Exception e) {
            //plugin.log(e.getMessage());
        }
        return result;
    }

    /**
     * Private Nachricht an einen Spieler senden
     *
     * @param player  Spieler, der die Nachricht sendet
     * @param reciver Spieler, der die Nachricht bekommt
     * @param message Nachricht, die der Spieler versendet
     */
    public void sendMsg(ProxiedPlayer player, ProxiedPlayer reciver, String message) {
        try {
            PreparedStatement statement = plugin.database.getConnection().prepareStatement("INSERT `tmczFriendsMessages` SET `sender` = ?, `reciver` = ?, `msg` = ?;");
            statement.setString(1, player.getDisplayName());
            statement.setString(2, reciver.getDisplayName());
            statement.setString(3, message);
            statement.executeUpdate();
            statement.close();
            player.sendMessage(plugin.formatFriendsChat("Du", reciver.getDisplayName(), message));
            reciver.sendMessage(plugin.formatFriendsChat(player.getDisplayName(), "Dir", message));
        } catch (Exception e) {

        }
    }
}
