package org.turbojax.config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
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
    PREFIX("prefix", "<gray>[%main_color%ChatFilter<gray>] <white>"),
    MAIN_COLOR("main-color", "<#FA8128>"),
    // Console logs
    // Supported placeholders: %prefix%, %main_color%, %file%
    CONFIG_BACKUP_SUCCESS("config-backup-success", "%prefix%Backed up the old %file% and loaded the new one."),
    // Supported placeholders: %prefix%, %main_color%, %file%
    CONFIG_BACKUP_FAIL("config-backup-fail", "%prefix%<red>Could not back up the old %file%."),
    // Supported placeholders: %prefix%, %main_color%, %file%
    CONFIG_LOADED("config-loaded", "%prefix%Successfully loaded %file%"),
    // Supported placeholders: %prefix%, %main_color%, %file%
    CONFIG_INVALID_YAML("config-invalid-yaml", "%prefix%<red>%file% contains an invalid YAML configuration.  Verify the contents of the file."),
    // Supported placeholders: %prefix%, %main_color%, %file%
    CONFIG_NOT_FOUND("config-not-found", "%prefix%<red>Could not find %file%.  Make sure it exists."),
    // Supported placeholders: %prefix%, %main_color%, %file%
    CONFIG_CANNOT_CREATE("config-cannot-create", "%prefix%Could not create %file%."),
    // Supported placeholders: %prefix%, %main_color%, %file%
    CONFIG_CREATED("config-created", "%prefix%Successfully created %file%."),
    // Supported placeholders: %prefix%, %main_color%, %file%, %key%
    KEY_NOT_FOUND("key-not-found", "%prefix%Could not find \"%key%\" in \"%file%\"."),
    // Supported placeholders: %prefix%, %main_color%, %url%
    VERSION_URI_ERROR("version-uri-error", "%prefix%Could not parse the github URL.  Contact Turbo."),

    // Supported placeholders: %prefix%, %main_color%, %file%
    CANNOT_READ_WORDLIST("cannot-read-wordlist", "%prefix%<red>Could not read the wordlist from %file%."),
    // Supported placeholders: %prefix%, %main_color%, %file%
    WORDLIST_RELOADED("wordlist-reloaded", "%prefix%The wordlist has been reloaded."),
    // Supported placeholders: %prefix%, %main_color%, %file%, %url%
    WORDLIST_OVERRIDDEN("wordlist-overridden", "%prefix%The wordlist will be overridden if it exists."),
    // Supported placeholders: %prefix%, %main_color%, %file%, %url%
    WORDLIST_CANNOT_SAVE("wordlist-cannot-save", "%prefix%Could not save the wordlist."),
    // Supported placeholders: %prefix%, %main_color%, %file%, %url%
    WORDLIST_SAVED("wordlist-saved", "%prefix%The wordlist has been saved."),
    // Supported placeholders: %prefix%, %main_color%, %file%, %url%
    WORDLIST_MALFORMED_URL("wordlist-malformed-url", "%prefix%The provided URL \"%url%\" is not a valid link."),
    // Supported placeholders: %prefix%, %main_color%, %file%, %url%
    WORDLIST_DOWNLOAD_ERROR("wordlist-download-error", "%prefix%Could not download the wordlist from \"%url%\""),

    // Chat logs
    // Supported placeholders: %prefix%, %main_color%, %command%, %label%
    NO_PERMISSION("no-permission", "%prefix%<red>You don't have permission to use this command!"),
    // Supported placeholders: %prefix%, %main_color%, %player%, %blocked_message%, %detected_words%, %where_blocked%
    DETECTION_MESSAGE("detection-message", "\n%main_color%&lChatFilter Detection\n\n&8▪ &fFound some blacklisted/banned words in\n&8▪ &fa message sent by %main_color%%player%&f.\n\n&8▪ &fFull message: %main_color%%blocked_message%\n&8▪ &fBlacklisted words: %main_color%%detected_words%\n&8▪ &fTyped in: %main_color%%where_blocked%\n"),
    // Supported placeholders: %prefix%, %main_color%, %word%
    COMMAND_ADD_WORD_SUCCESS("command-add-word-success", "%prefix%Added \"%word%\" to the blocked word list."),
    // Supported placeholders: %prefix%, %main_color%, %word%
    COMMAND_ADD_WORD_FAIL("command-add-word-fail", "%prefix%\"%word%\" is already a blocked word."),
    // Supported placeholders: %prefix%, %main_color%, %word%
    COMMAND_REMOVE_WORD_SUCCESS("command-remove-word-success", "%prefix%Removed \"%word%\" from the blocked word list."),
    // Supported placeholders: %prefix%, %main_color%, %word%
    COMMAND_REMOVE_WORD_FAIL("command-remove-word-fail", "%prefix%\"%word%\" is not a blocked word."),
    // Supported placeholders: %prefix%, %main_color%, %label%
    COMMAND_ADD_CORRECT_USAGE("command-add-correct-usage", "%prefix%<red>Usage: /%label% add <word>"),
    // Supported placeholders: %prefix%, %main_color%, %label%
    COMMAND_REMOVE_CORRECT_USAGE("command-remove-correct-usage", "%prefix%<red>Usage: /%label% remove <word>"),
    // Supported placeholders: %prefix%, %main_color%, %label%
    COMMAND_LIST_CORRECT_USAGE("command-list-correct-usage", "%prefix%<red>Usage: /%label% list [page]"),
    // Supported placeholders: %prefix%, %main_color%, %label%, %page%, %pages%
    COMMAND_LIST_PAGE_TOO_BIG("command-list-page-too-big", "%prefix%There are only %pages% pages.  %page% is too big!"),
    // Supported placeholders: %prefix%, %main_color%, %label%, %page%, %pages%
    COMMAND_LIST_HEADER("command-list-header", "%prefix%Blocked words:"),
    // Supported placeholders: %prefix%, %main_color%, %label%, %page%, %pages%
    COMMAND_LIST_FOOTER("command-list-footer", "Page %page%/%pages%"),
    // Supported placeholders: %prefix%, %main_color%, %label%
    COMMAND_RELOAD_CORRECT_USAGE("command-reload-correct-usage", "%prefix%<red>Usage: /%label% reload"),
    // Supported placeholders: %prefix%, %main_color%, %label%
    COMMAND_RELOAD_SUCCESS("command-reload-success", "%prefix%Reloaded configs and wordlist."),
    // Supported placeholders: %prefix%, %main_color%, %label%
    COMMAND_SAVE_SUCCESS("command-reload-success", "%prefix%Saved the wordlist."),
    // Supported placeholders: %prefix%, %main_color%, %label%
    COMMAND_HELP_CORRECT_USAGE("command-help-correct-usage", "%prefix%<red>Usage: /%label% help"),
    // Supported placeholders: %prefix%, %main_color%, %label%
    COMMAND_VERSION_CORRECT_USAGE("command-version-correct-usage", "%prefix%<red>Usage: /%label% version"),
    // Supported placeholders: %prefix%, %main_color%, %label%
    COMMAND_HELP_MESSAGE("command-help-message", "<gray><st>          </st><%main_color% ChatFilter Commands <gray>><st>          </st>\n\n%main_color%/%label% add <<word>> <white>- Adds a word to the blacklist.\n%main_color%/%label% list <white>- Lists the words on the blacklist.\n%main_color%/%label% remove <word> <white>- Removes a word from the blacklist.\n%main_color%/%label% reload <white>- Reload the ChatFilter plugin.\n%main_color%/%label% help <white>- Displays this help menu with command summaries.\n%main_color%/%label% version <white>- Shows the current plugin version and checks for a update.\n\n"),
    // Supported placeholders: %prefix%, %main_color%, %label%, %plugin_version%, %latest_version%
    HAS_UPDATE("has-update", "ChatFilter has an update!  You're on %plugin_version%.  Please update to %latest_version%."),
    // Supported placeholders: %prefix%, %main_color%, %label%, %plugin_version%, %latest_version%
    VERSION_MESSAGE("version-message", "ChatFilter is running version %plugin_version%."),
    // Supported placeholders: %player%, %name%, %new_name%, %word%
    FILTER_ANVIL_CENSOR_LOG("filter-anvil-censor-log", "%player% renamed an item to \"%name%\".  The name contains the blacklisted word \"%word%\".  The item was renamed to \"%new-name%\"."),
    // Supported placeholders: %prefix%, %main_color%, %player%, %name%, %new_name%, %word%
    FILTER_ANVIL_CENSOR_MESSAGE("filter-anvil-censor-message", "%prefix%%player% attempted to name an item to \"%name%\", containing the blacklisted word \"%word%\".  The item was renamed to \"%new-name%\""),
    // Supported placeholders: %prefix%, %main_color%, %player%, %name%, %new_name%, %word%
    FILTER_ANVIL_CENSOR_WARNING("filter-anvil-censor-warning", "%prefix%You cannot name an item \"%name%\" because the word \"%word%\" is blacklisted.  Your item was renamed to \"%new-name%\"."),
    // Supported placeholders: %player%, %name%, %word%
    FILTER_ANVIL_CANCEL_LOG("filter-anvil-cancel-log", "%player% renamed an item to \"%name%\".  The name contains the blacklisted word \"%word%\".  This event was cancelled."),
    // Supported placeholders: %prefix%, %main_color%, %player%, %name%, %word%
    FILTER_ANVIL_CANCEL_MESSAGE("filter-anvil-cancel-message", "%prefix%%player% attempted to name an item to \"%name%\", containing the blacklisted word \"%word%\""),
    // Supported placeholders: %prefix%, %main_color%, %player%, %name%, %word%
    FILTER_ANVIL_CANCEL_WARNING("filter-anvil-cancel-warning", "%prefix%You cannot rename an item to \"%name%\" because the word \"%word%\" is blacklisted."),

    // Supported placeholders: %player%, %message%, %new_message%, %word%
    FILTER_CHAT_CENSOR_LOG("filter-chat-censor-log", "%player% sent \"%message%\" in chat.  The message contains the blacklisted word \"%word%\".  The message was censored to \"%new_message%\"."),
    // Supported placeholders: %prefix%, %main_color%, %player%, %message%, %new_message%, %word%
    FILTER_CHAT_CENSOR_MESSAGE("filter-chat-censor-message", "%prefix%%player% sent \"%message%\" in chat.  The message contains the blacklisted word \"%word%\".  It was censored to \"%new_message%\"."),
    // Supported placeholders: %prefix%, %main_color%, %player%, %message%, %new_message%, %word%
    FILTER_CHAT_CENSOR_WARNING("filter-chat-censor-warning", "%prefix%<red>Your message contained a blacklisted word, \"%word%\".  Your message has been censored."),
    // Supported placeholders: %player%, %message%, %word%
    FILTER_CHAT_CANCEL_LOG("filter-chat-cancel-log", "%player% send \"%message%\" in chat.  The message contains the blacklisted word \"%word%\".  The chat message was cancelled."),
    // Supported placeholders: %prefix%, %main_color%, %player%, %message%, %word%
    FILTER_CHAT_CANCEL_MESSAGE("filter-chat-cancel-message", "%prefix%%player% sent \"%message%\" in chat.  The message contains the blacklisted word \"%word%\".  The chat message was cancelled"),
    // Supported placeholders: %prefix%, %main_color%, %player%, %message%, %word%
    FILTER_CHAT_CANCEL_WARNING("filter-chat-cancel-warning", "%prefix%<red>Your message contained a blacklisted word, \"%word%\", so it was not sent."),

    ;

    public static final File file = new File("plugins/ChatFilter/messages.yml");
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

        Map<String,String> placeholders = getCommonPlaceholders();
        placeholders.put("%file%", file.getName());

        // Loading the config
        try {
            config.load(file);

            sendToConsole(CONFIG_LOADED, placeholders);
        } catch (InvalidConfigurationException err) {
            sendToConsole(CONFIG_INVALID_YAML, placeholders);
            return false;
        } catch (IOException err) {
            sendToConsole(CONFIG_NOT_FOUND, placeholders);
            return false;
        }

        // Handling version mismatch
        if (!ChatFilter.getPluginVersion().equals(MainConfig.getVersion())) {
            try {
                // Backing up old configs
                Files.copy(file.toPath(), Paths.get(file.getPath() + ".old"));

                // Loading configs for this version
                ChatFilter.getInstance().saveResource(file.getName(), true);

                sendToConsole(CONFIG_BACKUP_SUCCESS, placeholders);
            } catch (IOException e) {
                sendToConsole(CONFIG_BACKUP_FAIL, placeholders);
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
     * @param replace Whether or not to replace the config file with the default configs.
     * @return Whether or not the file was created successfully.
     */
    public static boolean createFile(boolean replace) {
        // Skipping if the file already exists and replace is false
        if (file.exists() && !replace) return true;

        // Creating the file.
        ChatFilter.getInstance().saveResource(file.getName(), true);

        Map<String,String> placeholders = getCommonPlaceholders();
        placeholders.put("%file%", file.getName());

        // Checking if the file exists now.
        if (file.exists()) {
            Message.sendToConsole(Message.CONFIG_CREATED, placeholders);
            return true;
        }
        
        Message.sendToConsole(Message.CONFIG_CANNOT_CREATE, placeholders);
        return false;
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
            if (key != KEY_NOT_FOUND) {
                sendToConsole(getMessage(KEY_NOT_FOUND), Map.of("%key%", key.configKey, "%file%", file.getName()));
            }
            
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

    public static Map<String,String> getCommonPlaceholders() {
        return new HashMap<>(Map.of("%prefix%", getMessage(PREFIX), "%main_color%", getMessage(MAIN_COLOR)));
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

    public static Component toComponent(String message) {
        // Passing the string through legacy and minimessage serialization
        Component legacy = legacySerializer.deserialize(message);
        message = minimessageSerializer.serialize(legacy).replace("\\", "");

        return minimessageSerializer.deserialize(message);
    }

    public static Component toComponent(Message message, Map<String,String> placeholders) {
        return toComponent(applyPlaceholders(getMessage(message), placeholders));
    }

    /**
     * Converts a message to a component by passing it through legacy and minimessage formatting.
     *
     * @param message The string to convert.
     *
     * @return The component value of the message.
     */
    public static Component toComponent(String message, Map<String,String> placeholders) {
        return toComponent(applyPlaceholders(message, placeholders));
    }

    public static void sendToConsole(Message message) {
        send(Bukkit.getConsoleSender(), getMessage(message));
    }

    public static void sendToConsole(String message) {
        send(Bukkit.getConsoleSender(), message);
    }

    public static void sendToConsole(Message message, Map<String,String> placeholders) {
        send(Bukkit.getConsoleSender(), applyPlaceholders(getMessage(message), placeholders));
    }

    public static void sendToConsole(String message, Map<String,String> placeholders) {
        send(Bukkit.getConsoleSender(), applyPlaceholders(message, placeholders));
    }

    public static void send(Audience audience, Message message) {
        send(audience, getMessage(message));
    }

    public static void send(Audience audience, String message) {
        audience.sendMessage(toComponent(message));
    }

    public static void send(Audience audience, Message message, Map<String,String> placeholders) {
        send(audience, applyPlaceholders(getMessage(message), placeholders));
    }

    public static void send(Audience audience, String message, Map<String,String> placeholders) {
        send(audience, applyPlaceholders(message, placeholders));
    }

    public static String applyPlaceholders(String message, Map<String,String> placeholders) {
        boolean allPassed = false;
        while (!allPassed) {
            allPassed = true;

        for (Entry<String,String> placeholder : placeholders.entrySet()) {
                if (message.contains(placeholder.getKey())) allPassed = false;

            message = message.replace(placeholder.getKey(), placeholder.getValue());
            }
        }

        return message;
    }
}