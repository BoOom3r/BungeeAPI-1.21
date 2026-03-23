package net.boom3r.bungeeapi.commands.interfaces;


import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCommandManager {
    private final Plugin plugin;

    public BungeeCommandManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public void register(BungeeCommand cmd) {
        BungeeCommandWrapper wrapper = new BungeeCommandWrapper(cmd);
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, wrapper);
    }
}