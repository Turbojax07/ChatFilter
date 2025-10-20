package org.turbojax.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.turbojax.ChatFilter;

public enum Messages {
    PREFIX("prefix", "<gray>[%main_color%ClearLag<gray>] <white>"),
    MAIN_COLOR("main-color", "<#FA8128>"),
    CONFIG_BACKUP_FAIL("config-backup-fail", "%prefix%<red>Could not back up the old %file%."),
    CONFIG_BACKUP_SUCCESS("config-backup-success", "%prefix%Backed up the old %file% and loaded the new one."),
    CONFIG_LOADED("config-loaded", "%prefix%Successfully loaded %file%"),
    CONFIG_INVALID_YAML("config-invalid-yaml", "%prefix%<red>%file% contains an invalid YAML configuration.  Verify the contents of the file."),
    CONFIG_NOT_FOUND("config-not-found", "%prefix%<red>Could not find %file%.  Make sure it exists."),
    CONFIG_CANNOT_CREATE("config-cannot-create", "%prefix%Could not create %file%."),
    CONFIG_CREATED("config-created", "%prefix%Successfully created %file%."),
    NO_PERMISSION("no-permission", "%prefix%<red>You don't have permission to use this command!"),
    KEY_NOT_FOUND("key-not-found", "%prefix%Could not find %key% in the config."),
    DETECTION_MESSAGE("detection-message", "\n%main_color%&lChatFilter Detection\n\n&8▪ &fFound some blacklisted/banned words in\n&8▪ &fa message sent by %main_color%%player%&f.\n\n&8▪ &fFull message: %main_color%%blocked_message%\n&8▪ &fBlacklisted words: %main_color%%detected_words%\n&8▪ &fTyped in: %main_color%%where_blocked%\n")
    ;

    public static final File file = new File("plugins/ClearLag/messages.yml");
    public static final FileConfiguration config = new YamlConfiguration();

    public static final MiniMessage minimessageSerializer = MiniMessage.miniMessage();
    public static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();

    public final String configKey;
    public final String defaultValue;

    Messages(String configKey, String defaultValue) {
        this.configKey = configKey;
        this.defaultValue = defaultValue;
    }

    /**
     * Reloads the configuration.
     *
     * @return Whether the configuration was loaded successfully.
     */
    public static boolean load() {
        // Creating the file if it doesn't exist.
        // If the function returns false, the load function fails too.
        // Logging is handled by the function.
        if (!createFile(false)) {
            return false;
        }

        String msg;
        // Handling version mismatch
        if (!ChatFilter.getPluginVersion().equals(MainConfig.getVersion())) {
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
     * @param replace Whether or not to replace the config file with the default configs.
     * @return Whether or not the file was created successfully.
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

    /**
     * Gets a message from the config.
     *
     * @param key The message to retrieve.
     *
     * @return A message from the config
     */
    public static String getMessage(Messages key) {
        if (!config.contains(key.configKey)) {
            String msg;
            if (key == KEY_NOT_FOUND) {
                msg = key.defaultValue;
            } else {
                msg = getMessage(KEY_NOT_FOUND);
            }
            Bukkit.getConsoleSender().sendMessage(toComponent(msg.replace("%key%", key.configKey)));
            
            return key.defaultValue;
        }

        // If the config is a list, it converts it into a single string separated by newlines.
        // Otherwise, it just returns the string.
        if (config.isList(key.configKey)) {
            StringBuilder retVal = new StringBuilder();
            for (String line : config.getStringList(key.configKey)) {
                retVal.append(line).append("\n");
            }

            return retVal.substring(0, retVal.length() - 1);
        } else {
            return config.getString(key.configKey);
        }
    }

    /**
     * Converts a message to a component by passing it through legacy and minimessage formatting.
     *
     * @param message The message enum to convert.
     *
     * @return The component value of the message.
     */
    public static Component toComponent(Messages message) {
        return toComponent(getMessage(message));
    }

    /**
     * Converts a message to a component by passing it through legacy and minimessage formatting.
     * Also applies the default placeholders.
     *
     * @param message The string to convert.
     *
     * @return The component value of the message.
     */
    public static Component toComponent(String message) {
        // Applying global placeholders to the string.
        message.replaceAll("%prefix%", Messages.getMessage(PREFIX));
        message.replaceAll("%main_color%", Messages.getMessage(MAIN_COLOR));

        // Passing the string through legacy and minimessage serialization
        Component legacy = legacySerializer.deserialize(message);
        message = minimessageSerializer.serialize(legacy).replace("\\", "");

        return minimessageSerializer.deserialize(message);
    }
}