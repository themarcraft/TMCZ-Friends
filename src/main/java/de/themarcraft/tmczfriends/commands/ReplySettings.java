package de.themarcraft.tmczfriends.commands;

import de.themarcraft.tmczfriends.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class ReplySettings extends Command {

    Main plugin;

    public ReplySettings(Main plugin) {
        super("replysettings", "themarcraft.friends.settings.reply", "rs");
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
            plugin.playerSendFriendMessage(player, "Ändere deine Aktion für /reply");
            plugin.playerSendFriendMessage(player, "1 = Sende Nachricht an letzte Person, der du geschrieben hast");
            plugin.playerSendFriendMessage(player, "2 = Antworte auf einkommende Nachrichten");
        }
        if (args.length == 1) {
            switch (args[0]) {
                case "1":
                    plugin.database.setReplyType(player.getDisplayName(), false);
                    plugin.playerSendFriendMessage(player, "Du Sendest mit /r nun Nachrichten an die Person, der du zuletzt geschrieben hast");
                    break;
                case "2":
                    plugin.database.setReplyType(player.getDisplayName(), true);
                    plugin.playerSendFriendMessage(player, "Du Sendest mit /r nun Nachrichten an die Person, die dir zuletzt geschrieben hat");
                    break;
                default:
                    plugin.playerSendFriendMessage(player, "Ändere deine Aktion für /reply");
                    plugin.playerSendFriendMessage(player, "1 = Antworte auf einkommende Nachrichten");
                    plugin.playerSendFriendMessage(player, "2 = Sende Nachricht an letzte Person, der du geschrieben hast");
                    break;
            }
        }
    }
}
