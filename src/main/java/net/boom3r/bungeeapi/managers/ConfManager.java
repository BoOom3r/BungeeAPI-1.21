package net.boom3r.bungeeapi.managers;

import net.boom3r.bungeeapi.BungeeAPI;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;




public class ConfManager {


    Configuration configuration;
    private final Plugin plugin;

    public ConfManager(Plugin plugin) {

        this.plugin = plugin;
    }

    private void loadConfig() throws IOException {
        this.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(BungeeAPI.bungeeInstance.getDataFolder(), "config.yml"));
    }


    public void makeConfig() throws IOException {
        // Create plugin config folder if it doesn't exist
        if (!BungeeAPI.bungeeInstance.getDataFolder().exists()) {
            BungeeAPI.bungeeInstance.getLogger().info("Created config folder: " + BungeeAPI.bungeeInstance.getDataFolder().mkdir());
        }

        File configFile = new File(BungeeAPI.bungeeInstance.getDataFolder(), "config.yml");

        // Copy default config if it doesn't exist
        if (!configFile.exists()) {
            FileOutputStream outputStream = new FileOutputStream(configFile); // Throws IOException
            InputStream in = plugin.getClass().getClassLoader().getResourceAsStream("config.yml"); // This file must exist in the jar resources folder
            in.transferTo(outputStream); // Throws IOException
        }
        loadConfig();
    }


    public Configuration getConfig() {
        return configuration;
    }
}
