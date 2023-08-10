package org.me.synccommand.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.me.synccommand.shared.RedisHandler;

public class SyncCommand extends Command {

    public SyncCommand() {
        super("syncb");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(new TextComponent("Usage: /syncb <channel> <command>"));
            return;
        }

        String channel = args[0];
        String command = String.join(" ", args).substring(channel.length()).trim();

        RedisHandler.publish(channel, command);
        sender.sendMessage(new TextComponent("Command synchronized to the " + channel + " channel."));
    }
}
