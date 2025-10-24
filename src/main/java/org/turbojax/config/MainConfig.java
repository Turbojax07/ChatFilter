package org.turbojax.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
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

        // Loading the config
        try {
            config.load(file);

            Message.sendToConsole(Message.CONFIG_LOADED, Map.of("%file%", file.getName()));
            return true;
        } catch (InvalidConfigurationException err) {
            Message.sendToConsole(Message.CONFIG_INVALID_YAML, Map.of("%file%", file.getName()));
        } catch (IOException err) {
            Message.sendToConsole(Message.CONFIG_NOT_FOUND, Map.of("%file%", file.getName()));
        }

        // Handling version mismatch
        if (!ChatFilter.getPluginVersion().equals(getVersion())) {
            try {
                // Backing up old configs
                Files.copy(file.toPath(), Paths.get(file.getPath() + ".old"));

                // Loading configs for this version
                ChatFilter.getInstance().saveResource(file.getName(), true);

                Message.sendToConsole(Message.CONFIG_BACKUP_SUCCESS, Map.of("%file%", file.getName()));
            } catch (IOException e) {
                Message.sendToConsole(Message.CONFIG_BACKUP_FAIL, Map.of("%file%", file.getName()));
                return false;
            }
        }

        return true;
    }

    /**
     * Loads the configuration.
     * This does no logging or version checks, and it is recommended to run the load function after both configs have gone through an "unsafe" load.
     */
    public static void unsafeLoad() {
        // Creating the file if it doesn't exist.
        if (!file.exists()) {
            ChatFilter.getInstance().saveResource(file.getName(), false);
        }

        // Checking if the file still doesn't exist.
        if (!file.exists()) return;

        // Loading the config
        try {
            config.load(file);
        } catch (Exception err) {}

        return;
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

        // Checking if the file still doesn't exist.
        System.out.println("File exists: " + file.exists());
        if (!file.exists()) {
            Message.sendToConsole(Message.CONFIG_CANNOT_CREATE, Map.of("%file%", file.getName()));
            return false;
        }

        Message.sendToConsole(Message.CONFIG_CREATED, Map.of("%file%", file.getName()));
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

    public static String anvilDefaultName() {
        return config.getString("anvil-default-name");
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
        String v = config.getString("version");
        System.out.println(v);
        return v;
    }
}