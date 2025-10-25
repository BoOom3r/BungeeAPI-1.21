package net.boom3r.bungeeapi.managers;

import net.boom3r.bungeeapi.BungeeAPI;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;



public class ConfManager {


    Configuration configuration;

    public Configuration loadConfig() throws IOException {
        return ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(BungeeAPI.bungeeInstance.getDataFolder(), "config.yml"));
    }


    public void makeConfig() throws IOException {
        // Create plugin config folder if it doesn't exist
        if (!BungeeAPI.bungeeInstance.getDataFolder().exists()) {
            BungeeAPI.bungeeInstance.getLogger().info("Created config folder: " + BungeeAPI.bungeeInstance.getDataFolder().mkdir());
        }

        File configFile = new File(BungeeAPI.bungeeInstance.getDataFolder(), "config.yml");

    }


    public Configuration getConfiguration() {
        return configuration;
    }
}
