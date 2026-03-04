package net.boom3r.bungeeapi.runnables;

import net.boom3r.bungeeapi.core.managers.LogManager;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

import static net.boom3r.bungeeapi.BungeeAPI.bungeeLogger;
import static net.boom3r.bungeeapi.BungeeAPI.serverManager;

public class ScheduledRunner implements Runnable {

    private final Plugin plugin;
    private ScheduledTask task;

    // Fréquence en minutes
    private static final long PERIOD_MINUTES = 5;

    public ScheduledRunner(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Démarre le runner périodique.
     */
    public void start() {
        if (task != null) {
            plugin.getLogger().warning("Le runner est déjà démarré !");
            return;
        }

        // Planifie la tâche : exécution initiale après 0 minute, puis toutes les 5 minutes
        this.task = plugin.getProxy().getScheduler().schedule(
                plugin,
                this,                 // la tâche à exécuter
                0L,                   // délai avant la première exécution
                PERIOD_MINUTES,       // délai entre exécutions
                TimeUnit.MINUTES
        );
    }

    /**
     * Arrête le runner si il est en cours.
     */
    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
            plugin.getLogger().info("Le runner périodique a été arrêté.");
        }
    }

    /**
     * Contenu exécuté toutes les X minutes.
     */
    @Override
    public void run() {
        plugin.getLogger().info("Tâche périodique exécutée à " + System.currentTimeMillis());

        try {
            // ==============================
            // 👉 Mets ici ta logique récurrente :
            // ==============================

            // Exemple : vérifier le status des serveurs
            // MaintenanceManager.checkAllServers();

            // Exemple : purge d'une cache
            // CacheManager.cleanup();

            // Exemple : broadcast aux admins
            // AdminMessenger.sendToAdmins("Vérification périodique terminée.");
            if (serverManager.serverListWasModified()){
                serverManager.refreshServerInstance();
                serverManager.clearServerListUpdateFlag();
            }
            bungeeLogger.Info("Refresh exécutée à " + System.currentTimeMillis());

        } catch (Exception e) {
            plugin.getLogger().severe("Erreur pendant l’exécution du runner : " + e.getMessage());
            e.printStackTrace();
        }
    }
}