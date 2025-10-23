package org.turbojax.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.turbojax.ChatFilter;

public enum Message {
    PREFIX("prefix", "<gray>[%main_color%ClearLag<gray>] <white>"),
    MAIN_COLOR("main-color", "<#FA8128>"),
    // Console logs
    CONFIG_BACKUP_SUCCESS("config-backup-success", "<gray>[<#FA8128>ClearLag<gray>] <white>Backed up the old %file% and loaded the new one."),
    CONFIG_BACKUP_FAIL("config-backup-fail", "<gray>[<#FA8128>ClearLag<gray>] <white><red>Could not back up the old %file%."),
    CONFIG_LOADED("config-loaded", "<gray>[<#FA8128>ClearLag<gray>] <white>Successfully loaded %file%"),
    CONFIG_INVALID_YAML("config-invalid-yaml", "<gray>[<#FA8128>ClearLag<gray>] <white><red>%file% contains an invalid YAML configuration.  Verify the contents of the file."),
    CONFIG_NOT_FOUND("config-not-found", "<gray>[<#FA8128>ClearLag<gray>] <white><red>Could not find %file%.  Make sure it exists."),
    CONFIG_CANNOT_CREATE("config-cannot-create", "<gray>[<#FA8128>ClearLag<gray>] <white>Could not create %file%."),
    CONFIG_CREATED("config-created", "<gray>[<#FA8128>ClearLag<gray>] <white>Successfully created %file%."),
    KEY_NOT_FOUND("key-not-found", "<gray>[<#FA8128>ClearLag<gray>] <white>Could not find %key% in the config."),
    // Chat logs
    NO_PERMISSION("no-permission", "%prefix%<red>You don't have permission to use this command!"),
    DETECTION_MESSAGE("detection-message", "\n%main_color%&lChatFilter Detection\n\n&8▪ &fFound some blacklisted/banned words in\n&8▪ &fa message sent by %main_color%%player%&f.\n\n&8▪ &fFull message: %main_color%%blocked_message%\n&8▪ &fBlacklisted words: %main_color%%detected_words%\n&8▪ &fTyped in: %main_color%%where_blocked%\n")
    ;

    public static final File file = new File("plugins/ClearLag/messages.yml");
    public static final FileConfiguration config = new YamlConfiguration();

    public static final MiniMessage minimessageSerializer = MiniMessage.miniMessage();
    public static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacyAmpersand();
    public static final PlainTextComponentSerializer plaintext = PlainTextComponentSerializer.plainText();

    public final String configKey;
    public final String defaultValue;

    Message(String configKey, String defaultValue) {
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

        // Handling version mismatch
        if (!ChatFilter.getPluginVersion().equals(MainConfig.getVersion())) {
            try {
                // Backing up old configs
                Files.copy(file.toPath(), Paths.get(file.getPath() + ".old"));

                // Loading configs for this version
                ChatFilter.getInstance().saveResource("config.yml", true);

                sendToConsole(CONFIG_BACKUP_SUCCESS, Map.of("%file%", file.getName()));
            } catch (IOException e) {
                sendToConsole(CONFIG_BACKUP_FAIL, Map.of("%file%", file.getName()));
                return false;
            }
        }

        // Loading the config
        try {
            config.load(file);

            sendToConsole(CONFIG_LOADED, Map.of("%file%", file.getName()));
            return true;
        } catch (InvalidConfigurationException err) {
            sendToConsole(CONFIG_INVALID_YAML, Map.of("%file%", file.getName()));
        } catch (IOException err) {
            sendToConsole(CONFIG_NOT_FOUND, Map.of("%file%", file.getName()));
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

        // Checking if the file still doesn't exist.
        if (!file.exists()) {
            sendToConsole(CONFIG_CANNOT_CREATE, Map.of("%file%", file.getName()));
            return false;
        }

        sendToConsole(CONFIG_CREATED, Map.of("%file%", file.getName()));
        return true;
    }

    /**
     * Gets a message from the config.
     *
     * @param key The message to retrieve.
     *
     * @return A message from the config
     */
    public static String getMessage(Message key) {
        if (!config.contains(key.configKey)) {
            String msg = (key == KEY_NOT_FOUND) ? key.defaultValue : getMessage(key);
            sendToConsole(msg.replace("%key%", key.configKey));
            
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
    public static Component toComponent(Message message) {
        return toComponent(getMessage(message));
    }

    /**
     * Converts a message to a component by passing it through legacy and minimessage formatting.
     *
     * @param message The string to convert.
     *
     * @return The component value of the message.
     */
    public static Component toComponent(String message) {
        // Passing the string through legacy and minimessage serialization
        Component legacy = legacySerializer.deserialize(message);
        message = minimessageSerializer.serialize(legacy).replace("\\", "");

        return minimessageSerializer.deserialize(message);
    }

    public static Map<String,String> getCommonPlaceholders() {
        return Map.of("%prefix%", getMessage(PREFIX), "%main_color%", getMessage(MAIN_COLOR));
    }

    public static void sendToConsole(Message message) {
        send(Bukkit.getConsoleSender(), getMessage(message), Map.of());
    }

    public static void sendToConsole(String message) {
        send(Bukkit.getConsoleSender(), message, Map.of());
    }

    public static void sendToConsole(Message message, Map<String,String> placeholders) {
        send(Bukkit.getConsoleSender(), getMessage(message), placeholders);
    }

    public static void sendToConsole(String message, Map<String,String> placeholders) {
        send(Bukkit.getConsoleSender(), message, placeholders);
    }

    public static void send(Audience audience, Message message) {
        send(audience, getMessage(message), Map.of());
    }

    public static void send(Audience audience, String message) {
        send(audience, message, Map.of());
    }

    public static void send(Audience audience, Message message, Map<String,String> placeholders) {
        send(audience, getMessage(message), placeholders);
    }

    public static void send(Audience audience, String message, Map<String,String> placeholders) {
        for (Entry<String,String> placeholder : placeholders.entrySet()) {
            message = message.replace(placeholder.getKey(), placeholder.getValue());
        }

        audience.sendMessage(toComponent(message));
    }
}