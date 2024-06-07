package org.me.synccommand.velocity;

import com.velocitypowered.api.proxy.ProxyServer;
import org.me.synccommand.shared.ConsoleCommand;

public class VelocityConsoleCommand implements ConsoleCommand {

    private final ProxyServer proxy;

    public VelocityConsoleCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    /**
     * Execute a command as the console.
     *
     * @param command The command string to execute.
     */
    @Override
    public void executeCommand(String command) {
        proxy.getCommandManager().executeAsync(proxy.getConsoleCommandSource(), command);
    }
}
