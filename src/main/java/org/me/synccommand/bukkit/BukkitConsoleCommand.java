package org.me.synccommand.bukkit;

import org.bukkit.Bukkit;
import org.me.synccommand.shared.ConsoleCommand;

public class BukkitConsoleCommand implements ConsoleCommand {

    private final SyncCommandBukkit plugin;

    public BukkitConsoleCommand(SyncCommandBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public void executeCommand(String command) {
        plugin.getFoliaLib().getScheduler().runNextTick(task -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command));
    }
}