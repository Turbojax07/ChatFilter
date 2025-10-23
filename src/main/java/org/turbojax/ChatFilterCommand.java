package org.turbojax;

import java.util.List;
import java.util.stream.Stream;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
        if (!sender.hasPermission("chatfilter.use")) {
            Message.send(sender, Message.NO_PERMISSION, Message.getCommonPlaceholders());
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            // TODO: print help message
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                // Validating arguments
                if (args.length != 1) {
                    sender.sendMessage(Component.text("Usage: /chatfilter reload", NamedTextColor.RED));
                    break;
                }

                // Reloading the configs
                MainConfig.load();
                Message.load();

                // Redownloading the wordlist if configs allow
                if (MainConfig.useRemoteWordlist()) {
                    WordlistManager.redownload();
                }
                break;
            case "add":
                // Validating arguments
                if (args.length != 2) {
                    sender.sendMessage(Component.text("Usage: /chatfilter add <word>", NamedTextColor.RED));
                    break;
                }
                break;
            case "list":
                // Validating arguments
                if (args.length != 1) {
                    sender.sendMessage(Component.text("Usage: /chatfilter list", NamedTextColor.RED));
                    break;
                }

                sender.sendMessage("%prefix%: The blocked words are:");
                StringBuilder builder = new StringBuilder("- ");
                List<String> blockedWords = WordlistManager.getBlockedWords();
                for (int i = 0; i < blockedWords.size(); i++) {
                    // Printing the line and resetting the builder
                    if (builder.length() > 100) {
                        sender.sendMessage(builder.toString());
                        builder = new StringBuilder("- ");
                    }

                    // Adding the word to the line and putting a comma after all but the last word.
                    builder.append(blockedWords.get(i));
                    if (i != blockedWords.size() - 1) builder.append(", ");
                }

                break;
            case "remove":
                // Validating arguments
                if (args.length != 2) {
                    sender.sendMessage(Component.text("Usage: /chatfilter remove <word>", NamedTextColor.RED));
                    break;
                }
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) return Stream.of("help", "reload", "add", "list", "remove").filter(s -> s.startsWith(args[0])).toList();

        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) return WordlistManager.getBlockedWords().stream().filter(s -> s.startsWith(args[2])).toList();

        return List.of();
    }
}
