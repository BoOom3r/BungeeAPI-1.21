package net.boom3r.bungeeapi.commands.interfaces;

import net.md_5.bungee.api.CommandSender;

import java.util.List;

public interface BungeeCommand {
    String getName();
    String getPermission();                // permission Bungee (ex. "bungeeAPI.admin.servermanager")
    List<String> getAliases();
    void execute(CommandSender sender, String[] args);
    List<String> tabComplete(CommandSender sender, String[] args);
}
