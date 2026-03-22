package net.boom3r.bungeeapi.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.List;

public class BungeeCommandWrapper extends Command implements TabExecutor {
    private final BungeeCommand command;

    public BungeeCommandWrapper(BungeeCommand command) {
        super(command.getName(), command.getPermission(),
                command.getAliases().toArray(new String[0]));
        this.command = command;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        // Vérifie la permission avant d’exécuter
        if (command.getPermission() != null && !command.getPermission().isEmpty()
                && !sender.hasPermission(command.getPermission())) {
            sender.sendMessage("§cVous n'avez pas la permission d'exécuter cette commande.");
            return;
        }
        command.execute(sender, args);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        // Filtre aussi selon les permissions
        if (command.getPermission() != null && !command.getPermission().isEmpty()
                && !sender.hasPermission(command.getPermission())) {
            return List.of();
        }
        return command.tabComplete(sender, args);
    }
}