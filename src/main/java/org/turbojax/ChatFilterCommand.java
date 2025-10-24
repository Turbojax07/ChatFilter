package org.turbojax;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.turbojax.config.MainConfig;
import org.turbojax.config.Message;

public class ChatFilterCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Collecting placeholders
        Map<String,String> placeholders = Message.getCommonPlaceholders();
        placeholders.put("%label%", label);

        // Sending the help message if there are no arguments
        if (args.length == 0) {
            // Checking permissions
            if (!sender.hasPermission("chatfilter.command.help")) {
                Message.send(sender, Message.NO_PERMISSION, Message.getCommonPlaceholders());
                return true;
            }

            Message.send(sender, Message.COMMAND_HELP_MESSAGE, placeholders);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                // Checking permissions
                if (!sender.hasPermission("chatfilter.command.help")) {
                    Message.send(sender, Message.NO_PERMISSION, Message.getCommonPlaceholders());
                    return true;
                }

                // Validating arguments
                if (args.length != 1) {
                    Message.send(sender, Message.COMMAND_HELP_CORRECT_USAGE, placeholders);
                }

                // Sending the help message
                Message.send(sender, Message.COMMAND_HELP_MESSAGE, placeholders);
                break;
            case "reload":
                // Checking permissions
                if (!sender.hasPermission("chatfilter.command.reload")) {
                    Message.send(sender, Message.NO_PERMISSION, Message.getCommonPlaceholders());
                    return true;
                }

                // Validating arguments
                if (args.length != 1) {
                    Message.send(sender, Message.COMMAND_RELOAD_CORRECT_USAGE, placeholders);
                    break;
                }

                // Reloading configs
                MainConfig.load();
                Message.load();

                // Reloading wordlist
                WordlistManager.reload();
                Message.send(sender, Message.COMMAND_RELOAD_SUCCESS, placeholders);
                break;
            case "save":
                // Checking permissions
                if (!sender.hasPermission("chatfilter.command.save")) {
                    Message.send(sender, Message.NO_PERMISSION, Message.getCommonPlaceholders());
                    return true;
                }
                
                // Validating arguments
                if (args.length != 2) {
                    Message.send(sender, Message.COMMAND_SAVE_CORRECT_USAGE, placeholders);
                    break;
                }

                WordlistManager.save();
                Message.send(sender, Message.COMMAND_SAVE_SUCCESS, placeholders);
                break;
            case "add":
                // Checking permissions
                if (!sender.hasPermission("chatfilter.command.add")) {
                    Message.send(sender, Message.NO_PERMISSION, Message.getCommonPlaceholders());
                    return true;
                }
                
                // Validating arguments
                if (args.length != 2) {
                    Message.send(sender, Message.COMMAND_ADD_CORRECT_USAGE, placeholders);
                    break;
                }

                // Getting placeholders
                placeholders.put("%word%", args[1]);

                // Adding the word to the wordlist
                if (WordlistManager.addWord(args[1])) {
                    Message.send(sender, Message.COMMAND_ADD_WORD_SUCCESS, placeholders);
                } else {
                    Message.send(sender, Message.COMMAND_ADD_WORD_FAIL, placeholders);
                }
                break;
            case "list":
                // Checking permissions
                if (!sender.hasPermission("chatfilter.command.list")) {
                    Message.send(sender, Message.NO_PERMISSION, Message.getCommonPlaceholders());
                    return true;
                }

                // Validating arguments

                if (args.length > 2) {
                    Message.send(sender, Message.COMMAND_LIST_CORRECT_USAGE, placeholders);
                    break;
                }

                int page = 0;
                if (args.length == 2) {
                    try {
                        page = Integer.parseInt(args[1]) - 1;
                    } catch (NumberFormatException e) {
                        Message.send(sender, Message.COMMAND_LIST_CORRECT_USAGE, placeholders);
                        break;
                    }
                }

                // Adding some placeholders
                placeholders.put("%page%", String.valueOf(page + 1));
                
                // Listing the blocked words
                Message.send(sender, Message.COMMAND_LIST_HEADER, placeholders);
                StringBuilder builder = new StringBuilder("- ");
                List<String> blockedWords = WordlistManager.getBlockedWords();
                List<String> lines = new ArrayList<>();
                for (int i = 0; i < blockedWords.size(); i++) {
                    // Printing the line and resetting the builder
                    if (builder.length() > 100) {
                        lines.add(builder.toString());
                        builder = new StringBuilder("- ");
                    }

                    // Adding the word to the line and putting a comma after all but the last word.
                    builder.append(blockedWords.get(i));
                    if (i != blockedWords.size() - 1) builder.append(", ");
                }

                placeholders.put("%pages%", String.valueOf(lines.size() % 10 + 1));

                if (page > lines.size() / 10 + 1) {
                    Message.send(sender, Message.COMMAND_LIST_PAGE_TOO_BIG, placeholders);
                }

                for (int i = page * 10; i < page * 10 + 10; i++) {
                    if (i >= lines.size()) continue;
                    sender.sendMessage(lines.get(i));
                }

                Message.send(sender, Message.COMMAND_LIST_FOOTER, placeholders);

                break;
            case "remove":
                // Checking permissions
                if (!sender.hasPermission("chatfilter.command.remove")) {
                    Message.send(sender, Message.NO_PERMISSION, Message.getCommonPlaceholders());
                    return true;
                }

                // Validating arguments
                if (args.length != 2) {
                    Message.send(sender, Message.COMMAND_REMOVE_CORRECT_USAGE, placeholders);
                    break;
                }

                // Getting placeholders
                placeholders.put("%word%", args[1]);

                // Removing the word to the wordlist
                if (WordlistManager.removeWord(args[1])) {
                    Message.send(sender, Message.COMMAND_REMOVE_WORD_SUCCESS, placeholders);
                } else {
                    Message.send(sender, Message.COMMAND_REMOVE_WORD_FAIL, placeholders);
                }
                break;
            case "version":
                // Checking permissions
                if (!sender.hasPermission("chatfilter.command.version")) {
                    Message.send(sender, Message.NO_PERMISSION, Message.getCommonPlaceholders());
                    return true;
                }

                // Validating arguments
                if (args.length != 1) {
                    Message.send(sender, Message.COMMAND_VERSION_CORRECT_USAGE, placeholders);
                    break;
                }

                // Collecting placeholders
                placeholders.put("%plugin_version%", ChatFilter.getPluginVersion());
                placeholders.put("%latest_version%", ChatFilter.getLatestVersion());
                
                // Printing the version number
                Message.send(sender, ChatFilter.hasUpdate() ? Message.HAS_UPDATE : Message.VERSION_MESSAGE, placeholders);
                break;
            default:
                // Checking permissions
                if (!sender.hasPermission("chatfilter.command.help")) {
                    Message.send(sender, Message.NO_PERMISSION, Message.getCommonPlaceholders());
                    return true;
                }

                // Sending the help message
                Message.send(sender, Message.COMMAND_HELP_MESSAGE, placeholders);
                break;
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>();
            if (sender.hasPermission("chatfilter.command.help")) suggestions.add("help");
            if (sender.hasPermission("chatfilter.command.add")) suggestions.add("add");
            if (sender.hasPermission("chatfilter.command.list")) suggestions.add("list");
            if (sender.hasPermission("chatfilter.command.reload")) suggestions.add("reload");
            if (sender.hasPermission("chatfilter.command.remove")) suggestions.add("remove");
            if (sender.hasPermission("chatfilter.command.version")) suggestions.add("version");
            if (sender.hasPermission("chatfilter.command.save")) suggestions.add("save");
            return suggestions.stream().filter(s -> s.startsWith(args[0])).sorted().toList();
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("remove") && sender.hasPermission("chatfilter.command.remove")) {
            return WordlistManager.getBlockedWords().stream().filter(s -> s.startsWith(args[1])).sorted().toList();
        }

        return List.of();
    }
}
