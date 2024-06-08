package org.me.synccommand.bukkit;

import org.bukkit.Bukkit;
import org.me.synccommand.shared.ConsoleCommand;

public class BukkitConsoleCommand implements ConsoleCommand {

    private final SyncCommandBukkit plugin;

    public BukkitConsoleCommand(SyncCommandBukkit plugin) {
        this.plugin = plugin;
    }

    /**
     * Execute a command as the console.
     *
     * @param command The command string to execute.
     */
    @Override
    public void executeCommand(String command) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        });
    }
}
