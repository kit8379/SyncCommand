package org.me.synccommand.velocity;

import org.me.synccommand.shared.ConsoleCommand;

import com.velocitypowered.api.proxy.ProxyServer;

public class VelocityConsoleCommand implements ConsoleCommand {

    private final ProxyServer proxy;

    public VelocityConsoleCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    /**
     * Execute a command as the console.
     * @param command The command string to execute.
     */
    @Override
    public void executeCommand(String command) {
        proxy.getCommandManager().executeAsync(proxy.getConsoleCommandSource(), command);
    }
}
