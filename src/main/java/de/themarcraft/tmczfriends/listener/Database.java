package de.themarcraft.tmczfriends.listener;

import de.themarcraft.tmczfriends.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.*;

public class Database {

    Main plugin;
    private Connection connection;

    public Database(Main plugin) {
        this.plugin = plugin;
    }


    public Connection getConnection() throws SQLException {

        if (connection != null) {
            return connection;
        }

        String host = plugin.configuration.getString("config.database.host");
        String database = plugin.configuration.getString("config.database.database");
        String user = plugin.configuration.getString("config.database.user");
        String passwd = plugin.configuration.getString("config.database.passwd");

        Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, passwd);

        this.connection = connection;

        return connection;
    }

    public void initializeDatabase() throws SQLException {

        Statement statement = getConnection().createStatement();

        //Create the player_stats table
        String createMessagingSystem = "CREATE TABLE IF NOT EXISTS `tmczFriendsMessages` (`id` INT NOT NULL AUTO_INCREMENT , PRIMARY KEY (`id`),`sender` VARCHAR(255),`reciver` VARCHAR(255), `msg` VARCHAR(255) NUlL) ENGINE = InnoDB;";
        String createPlayerSettings = "CREATE TABLE IF NOT EXISTS `tmczFriendsSettings` (`player` varchar(255) NOT NULL, `replyType` tinyint(1) NOT NULL COMMENT 'false = ReplyToRecive, true = replyToSend', `friendJoinLeave` tinyint(1) NOT NULL DEFAULT 1, `friendSwitch` tinyint(1) NOT NULL DEFAULT 1, `msg` tinyint(1) NOT NULL DEFAULT 2 COMMENT '0 = Keiner, 1 = Freunde, 2 = Jeder', PRIMARY KEY (`player`), UNIQUE KEY `player` (`player`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
        String createFriendsList = "CREATE TABLE IF NOT EXISTS `tmczFriendsFriends` (`player` varchar(255) NOT NULL, `friends` text DEFAULT NULL, `lastOnline` TIMESTAMP NULL DEFAULT NULL, UNIQUE KEY `player` (`player`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;";
        String createRequestList = "CREATE TABLE IF NOT EXISTS `tmczFriendsRequests` (`id` INT(255) NOT NULL AUTO_INCREMENT, `player` VARCHAR(255) NOT NULL, `reciver` VARCHAR(255) NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `id` (`id`)) ENGINE=InnoDB;";


        statement.execute(createMessagingSystem);
        statement.execute(createPlayerSettings);
        statement.execute(createFriendsList);
        statement.execute(createRequestList);

        statement.close();

    }

    public String getPrefix() {
        try {
            PreparedStatement statement = plugin.database.getConnection().prepareStatement("SELECT * FROM tmczSettings");

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return resultSet.getString("prefix");

        } catch (SQLException e) {
            return null;
        }
    }

    public String getNoPermission() {
        try {
            PreparedStatement statement = plugin.database.getConnection().prepareStatement("SELECT * FROM tmczSettings");

            ResultSet resultSet = statement.executeQuery();
            return resultSet.getString("nopermission");

        } catch (SQLException e) {
            return null;
        }
    }

    public String getPlayerOnly() {
        /*try {
            PreparedStatement statement = plugin.database.getConnection().prepareStatement("SELECT * FROM tmczSettings");

            ResultSet resultSet = statement.executeQuery();
            return resultSet.getString("playeronly");

        } catch (SQLException e) {
            return null;
        }*/
        return plugin.configuration.getString("config.messages.playerOnly");
    }

    public String getInvalidPlayer() {
        /*try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM tmczSettings");

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            String result = resultSet.getString("validplayer");
            statement.close();
            return result;

        } catch (SQLException e) {
            plugin.log(e.getMessage());
            return null;
        }*/
        return plugin.configuration.getString("config.messages.invalidPlayer");
    }

    public boolean getReplyType(String player) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM tmczFriendsSettings WHERE player = ?;");
            statement.setString(1, player);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Boolean result = resultSet.getBoolean("replyType");
            statement.close();
            return result;

        } catch (SQLException e) {
            try {
                saveDefault(player);
            } catch (Exception e2) {
                plugin.log("E1: " + e.getMessage());
                plugin.log("E2: " + e2.getMessage());
            }
            return false;
        }
    }

    public boolean getFriendJoinSetting(String player) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM tmczFriendsSettings WHERE player = ?;");
            statement.setString(1, player);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Boolean result = resultSet.getBoolean("friendJoinLeave");
            statement.close();
            return result;

        } catch (SQLException e) {
            try {
                saveDefault(player);
            } catch (Exception e2) {
                plugin.log("E1: " + e.getMessage());
                plugin.log("E2: " + e2.getMessage());
            }
            return false;
        }
    }

    public boolean getFriendSwitchSetting(String player) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM tmczFriendsSettings WHERE player = ?;");
            statement.setString(1, player);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            Boolean result = resultSet.getBoolean("friendSwitch");
            statement.close();
            return result;

        } catch (SQLException e) {
            try {
                saveDefault(player);
            } catch (Exception e2) {
                plugin.log("E1: " + e.getMessage());
                plugin.log("E2: " + e2.getMessage());
            }
            return false;
        }
    }

    public int getMessageSetting(String player) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM tmczFriendsSettings WHERE player = ?;");
            statement.setString(1, player);

            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            int result = resultSet.getInt("msg");
            statement.close();
            return result;

        } catch (SQLException e) {
            try {
                saveDefault(player);
            } catch (Exception e2) {
                plugin.log("E1: " + e.getMessage());
                plugin.log("E2: " + e2.getMessage());
            }
            return 2;
        }
    }

    public void setReplyType(String player, Boolean value) {
        try {
            PreparedStatement save = plugin.database.getConnection().prepareStatement("UPDATE `tmczFriendsSettings` SET `replyType` = ? WHERE `tmczFriendsSettings`.`player` = ?;");
            save.setBoolean(1, value);
            save.setString(2, player);
            save.executeUpdate();
            save.close();
        } catch (SQLException e) {
            try {
                saveDefault(player);
            } catch (Exception e2) {
                plugin.log("E1: " + e.getMessage());
                plugin.log("E2: " + e2.getMessage());
            }
        }
    }

    public void setFriendJoinSetting(String player, Boolean value) {
        try {
            PreparedStatement save = plugin.database.getConnection().prepareStatement("UPDATE `tmczFriendsSettings` SET `friendJoinLeave` = ? WHERE `tmczFriendsSettings`.`player` = ?;");
            save.setBoolean(1, value);
            save.setString(2, player);
            save.executeUpdate();
            save.close();
        } catch (SQLException e) {
            try {
                saveDefault(player);
            } catch (Exception e2) {
                plugin.log("E1: " + e.getMessage());
                plugin.log("E2: " + e2.getMessage());
            }
        }
    }

    public void setFriendSwitchSetting(String player, Boolean value) {
        try {
            PreparedStatement save = plugin.database.getConnection().prepareStatement("UPDATE `tmczFriendsSettings` SET `friendSwitch` = ? WHERE `tmczFriendsSettings`.`player` = ?;");
            save.setBoolean(1, value);
            save.setString(2, player);
            save.executeUpdate();
            save.close();
        } catch (SQLException e) {
            try {
                saveDefault(player);
            } catch (Exception e2) {
                plugin.log("E1: " + e.getMessage());
                plugin.log("E2: " + e2.getMessage());
            }
        }
    }

    public void setMessageSetting(String player, int value) {
        try {
            PreparedStatement save = plugin.database.getConnection().prepareStatement("UPDATE `tmczFriendsSettings` SET `msg` = ? WHERE `tmczFriendsSettings`.`player` = ?;");
            save.setInt(1, value);
            save.setString(2, player);
            save.executeUpdate();
            save.close();
        } catch (SQLException e) {
            try {
                saveDefault(player);
            } catch (Exception e2) {
                plugin.log("E1: " + e.getMessage());
                plugin.log("E2: " + e2.getMessage());
            }
        }
    }

    public void saveDefault(String player) {
        try {
            PreparedStatement save2 = plugin.database.getConnection().prepareStatement("INSERT INTO `tmczFriendsSettings` (`player`, `friendSwitch`, `friendJoin`, `replyType`, `msg`) VALUES (?, ?, ?, ?);");
            save2.setString(1, player);
            save2.setInt(2, 0);
            save2.setInt(3, 0);
            save2.setInt(4, 0);
            save2.setInt(5, 2);
            save2.executeUpdate();
            save2.close();
        } catch (SQLException e) {

        }
    }
}
