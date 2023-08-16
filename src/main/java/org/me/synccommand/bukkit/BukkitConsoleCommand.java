package org.me.synccommand.bukkit;

import org.me.synccommand.shared.ConsoleCommand;

import org.bukkit.Bukkit;

public class BukkitConsoleCommand implements ConsoleCommand {

    /**
     * Execute a command as the console.
     * @param command The command string to execute.
     */
    @Override
    public void executeCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
