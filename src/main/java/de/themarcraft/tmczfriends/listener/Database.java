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

        //Try to connect to my MySQL database running locally
        String host = "127.0.0.1";
        String database = "tmcz_Network";
        String user = "root";
        String passwd = "marvin1234";

        Connection connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, passwd);

        this.connection = connection;

        return connection;
    }

    public void initializeDatabase() throws SQLException {

        Statement statement = getConnection().createStatement();

        //Create the player_stats table
        String createMessagingSystem = "CREATE TABLE IF NOT EXISTS `tmczFriendsMessages` (`id` INT NOT NULL AUTO_INCREMENT , PRIMARY KEY (`id`),`sender` VARCHAR(255),`reciver` VARCHAR(255), `msg` VARCHAR(255) NUlL) ENGINE = InnoDB;";
        String createPlayerSettings = "CREATE TABLE IF NOT EXISTS `tmczFriendsSettings` (`player` VARCHAR(255) NOT NULL , `replyType` BOOLEAN NOT NULL COMMENT 'false = ReplyToRecive, true = replyToSend' , UNIQUE (`player`)) ENGINE = InnoDB;";


        statement.execute(createMessagingSystem);
        statement.execute(createPlayerSettings);

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
        try {
            PreparedStatement statement = plugin.database.getConnection().prepareStatement("SELECT * FROM tmczSettings");

            ResultSet resultSet = statement.executeQuery();
            return resultSet.getString("playeronly");

        } catch (SQLException e) {
            return null;
        }
    }

    public String getInvalidPlayer() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM tmczSettings");

            ResultSet resultSet = statement.executeQuery();
            return resultSet.getString("validplayer");

        } catch (SQLException e) {
            return null;
        }
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
                PreparedStatement save = plugin.database.getConnection().prepareStatement("INSERT INTO `tmczFriendsSettings` (`player`, `replyType`) VALUES (?, ?);");
                save.setString(1, player);
                save.setBoolean(2, false);
                save.executeUpdate();
                save.close();
            } catch (SQLException e2) {
                plugin.log("SQLException 2: " + e2.getMessage());
            }
            plugin.log("SQLException 1: " + e.getMessage());
            return false;
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

                PreparedStatement save = plugin.database.getConnection().prepareStatement("INSERT INTO `tmczFriendsSettings` (`player`, `replyType`) VALUES (?, ?);");
                save.setString(1, player);
                save.setBoolean(2, false);
                save.executeUpdate();
                save.close();
            } catch (SQLException e2) {
                plugin.log("E1: " + e.getMessage());
                plugin.log("E2: " + e2.getMessage());
            }
        }
    }
}
