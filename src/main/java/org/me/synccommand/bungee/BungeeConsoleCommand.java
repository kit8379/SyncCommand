package org.me.synccommand.bungee;

import net.md_5.bungee.api.ProxyServer;
import org.me.synccommand.shared.ConsoleCommand;

public class BungeeConsoleCommand implements ConsoleCommand {

    private final ProxyServer proxy;

    public BungeeConsoleCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    /**
     * Execute a command as the console.
     *
     * @param command The command string to execute.
     */
    @Override
    public void executeCommand(String command) {
        proxy.getPluginManager().dispatchCommand(proxy.getConsole(), command);
    }
}
