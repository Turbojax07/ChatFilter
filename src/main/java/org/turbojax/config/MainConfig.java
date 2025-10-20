package org.turbojax.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.turbojax.ChatFilter;

public class MainConfig {
    public static final File file = new File("plugins/ChatFilter/config.yml");
    public static final FileConfiguration config = new YamlConfiguration();

    /**
     * Reloads the configuration.
     *
     * @return Whether the configuration was loaded successfully.
     */
    public static boolean load() {
        // Creating the file if it doesn't exist.
        // If the function returns false, the load function fails too.
        if (!createFile(false)) {
            return false;
        }

        String msg;

        // Handling version mismatch
        if (!ChatFilter.getPluginVersion().equals(getVersion())) {
            try {
                // Backing up old configs
                Files.copy(file.toPath(), Paths.get(file.getPath() + ".old"));

                // Loading configs for this version
                ChatFilter.getInstance().saveResource("config.yml", true);

                msg = Messages.getMessage(Messages.CONFIG_BACKUP_SUCCESS);
                msg = msg.replace("%file%", file.getName());
                Bukkit.getConsoleSender().sendMessage(Messages.toComponent(msg));
            } catch (IOException e) {
                msg = Messages.getMessage(Messages.CONFIG_BACKUP_FAIL);
                msg = msg.replace("%file%", file.getName());
                Bukkit.getConsoleSender().sendMessage(Messages.toComponent(msg));
                return false;
            }
        }

        // Loading the config
        try {
            config.load(file);

            msg = Messages.getMessage(Messages.CONFIG_LOADED);
            msg = msg.replace("%file%", file.getName());
            Bukkit.getConsoleSender().sendMessage(Messages.toComponent(msg));
            return true;
        } catch (InvalidConfigurationException err) {
            msg = Messages.getMessage(Messages.CONFIG_INVALID_YAML);
            msg = msg.replace("%file%", file.getName());
            Bukkit.getConsoleSender().sendMessage(Messages.toComponent(msg));
        } catch (IOException err) {
            msg = Messages.getMessage(Messages.CONFIG_NOT_FOUND);
            msg = msg.replace("%file%", file.getName());
            Bukkit.getConsoleSender().sendMessage(Messages.toComponent(msg));
        }

        return false;
    }

    /**
     * Creating the config file. If it doesn't exist, it loads the default config. If the file does
     * exist, it will only replace it if the parameter is true.
     *
     * @param replace Whether to replace the config file with the default configs or not.
     * @return Whether the file was created successfully or not.
     */
    public static boolean createFile(boolean replace) {
        // Creating the file if it doesn't exist.
        if (!file.exists()) {
            ChatFilter.getInstance().saveResource(file.getName(), replace);
        }

        String msg;
        // Checking if the file still doesn't exist.
        if (!file.exists()) {
            msg = Messages.getMessage(Messages.CONFIG_CANNOT_CREATE);
            msg = msg.replace("%file%", file.getName());
            Bukkit.getConsoleSender().sendMessage(Messages.toComponent(msg));
            return false;
        }

        msg = Messages.getMessage(Messages.CONFIG_CREATED);
        msg = msg.replace("%file%", file.getName());
        Bukkit.getConsoleSender().sendMessage(Messages.toComponent(msg));
        return true;
    }

    public static boolean useRemoteWordlist() {
        return config.getBoolean("use-remote-wordlist", true);
    }

    public static String wordlistFile() {
        return config.getString("wordlist-file", "wordlist.txt");
    }

    public static String wordlistUrl() {
        return config.getString("wordlist-url", "https://raw.githubusercontent.com/coffee-and-fun/google-profanity-words/main/data/en.txt");
    }

    public static boolean overrideWordlistFile() {
        return config.getBoolean("override-wordlist-file", false);
    }

    public static boolean detectLinks() {
        return config.getBoolean("detect-links", true);
    }

    public static String handleChatMessage() {
        return config.getString("handle-chat-message", "censor");
    }

    public static String handleAnvilRename() {
        return config.getString("handle-anvil-rename", "censor");
    }

    public static boolean logDetectionsToPlayers() {
        return config.getBoolean("log-detections-to-players", true);
    }

    public static boolean logDetectionsToFile() {
        return config.getBoolean("log-detections-to-file", true);
    }

    /**
     * Gets the version number of the config.
     *
     * @return The version number defined by the config.
     */
    public static String getVersion() {
        return config.getString("version");
    }
}