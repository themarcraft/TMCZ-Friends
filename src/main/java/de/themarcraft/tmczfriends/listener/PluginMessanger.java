package de.themarcraft.tmczfriends.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.themarcraft.tmczfriends.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PluginMessanger implements Listener {

    Main plugin;

    public PluginMessanger(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {

        ByteArrayDataInput data;
        String subChannel;
        try {
            data = ByteStreams.newDataInput(event.getData());
            subChannel = data.readUTF();
        } catch (Exception e) {
            return;
        }

        try {

            String player;
            String handle;

            switch (subChannel) {
                case "remove":
                    player = data.readUTF();
                    handle = data.readUTF();
                    plugin.friend.remove(player, handle);
                    sendUi(player);
                    break;
                case "add":
                    player = data.readUTF();
                    handle = data.readUTF();
                    plugin.friend.add(player, handle);
                    sendUi(player);
                    break;
                case "refuse":
                    player = data.readUTF();
                    handle = data.readUTF();
                    if (plugin.friend.refuse(player, handle)) {
                        plugin.playerSendFriendMessage(plugin.getProxy().getPlayer(player), "Du hast erfolgreich die Freundschafts-Anfrage von " + ChatColor.AQUA + handle + ChatColor.GRAY + " abgelehnt");
                    } else {
                        plugin.playerSendFriendMessage(plugin.getProxy().getPlayer(player), "Du hast von " + ChatColor.AQUA + handle + ChatColor.GRAY + " keine Freundschafts-Anfrage bekommen");
                    }
                    break;
                case "accept":
                    player = data.readUTF();
                    handle = data.readUTF();
                    if (plugin.friend.isRequest(player, handle) == 1) {
                        plugin.friend.refuse(player, handle);
                        plugin.friend.add(player, handle);
                        plugin.playerSendFriendMessage(plugin.getProxy().getPlayer(player), "Du bist nun mit " + ChatColor.AQUA + handle + ChatColor.GRAY + " befreundet");
                        try {
                            plugin.playerSendFriendMessage(plugin.getProxy().getPlayer(handle), ChatColor.AQUA + player + ChatColor.GRAY + " hat deine Freundschafts-Anfrage angenommen");
                        } catch (Exception e) {

                        }
                    } else {
                        plugin.playerSendFriendMessage(plugin.getProxy().getPlayer(player), ChatColor.AQUA + handle + ChatColor.GRAY + " hat dir keine Freundschafts-Anfrage geschickt");
                    }
                    break;
            }
        } catch (Exception e) {
            plugin.log2("================================================");
            plugin.log2("TMCZ - ERROR HANDLING");
            plugin.log2(e.getMessage());
            plugin.log2(e.getCause().getMessage());
            plugin.log2(e.fillInStackTrace().toString());
            plugin.log2("================================================");
        }
    }

    public void sendUi(String player) {
        sendPluginMesssage("friendui", player);
    }

    public void sendPluginMesssage(String msg, String player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(msg);
        out.writeUTF(plugin.friend.getFriends(player));
        String numberFriends;
        if (plugin.friend.getFriends(player).contains(",")) {
            numberFriends = String.valueOf(plugin.friend.getFriends(player).split(",").length);
        } else {
            numberFriends = "0";
        }
        out.writeUTF(numberFriends);
        String onlineFriends = "";
        for (ProxiedPlayer online : plugin.getProxy().getPlayers()) {
            if (plugin.friend.isFriend(player, online.getDisplayName())) {
                onlineFriends = onlineFriends + online.getDisplayName() + ",";
            }
        }
        out.writeUTF(onlineFriends);
        out.writeUTF(plugin.friend.getMax(player) + "");
        out.writeUTF(String.valueOf(plugin.friend.getRequestsInt(player)));
        out.writeUTF(plugin.friend.getRequests(player));
        String onlinePlayers = "";
        for (ProxiedPlayer online : plugin.getProxy().getPlayers()) {
            onlinePlayers = onlinePlayers + online.getDisplayName() + ",";
        }
        out.writeUTF(onlinePlayers);

        out.writeUTF(plugin.database.getReplyType(player) + "");
        out.writeUTF(plugin.database.getMessageSetting(player) + "");
        out.writeUTF(plugin.database.getFriendJoinSetting(player) + "");
        out.writeUTF(plugin.database.getFriendSwitchSetting(player) + "");
        plugin.getProxy().getPlayer(player).getServer().sendData("tmcz:friends", out.toByteArray());
    }

}
