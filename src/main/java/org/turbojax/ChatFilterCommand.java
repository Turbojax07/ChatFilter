package org.turbojax;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
import org.turbojax.config.Messages;

public class ChatFilterCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("chatfilter.use")) {
            sender.sendMessage(Messages.toComponent(Messages.NO_PERMISSION));
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
                Messages.load();

                // Redownloading the wordlist if configs allow
                if (MainConfig.useRemoteWordlist()) {
                    try {
                        // Downloading the wordlist
                        if (MainConfig.overrideWordlistFile()) {
                            // TODO: WORDLIST_OVERRIDDEN("wordlist-overridden", "%prefix%The wordlist will be overridden if it exists.")
                            Files.copy(new URI(MainConfig.wordlistUrl()).toURL().openStream(), ChatFilter.wordlist.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } else {
                            Files.copy(new URI(MainConfig.wordlistUrl()).toURL().openStream(), ChatFilter.wordlist.toPath());
                        }
                        // TODO: WORDLIST_SAVED("wordlist-saved", "%prefix%The wordlist has been saved.")
                    } catch(FileAlreadyExistsException e) {
                        // TODO: WORDLIST_OVERRIDE_STOPPED("wordlist-override-stopped", "%prefix%Cannot override the wordlist with the remote copy.")
                    } catch (URISyntaxException | MalformedURLException e) {
                        // TODO: WORDLIST_MALFORMED_URL("wordlist-malformed-url", "The provided URL \"%url%\" is not a valid link.")
                    } catch (IOException e) {
                        // TODO: WORDLIST_DOWNLOAD_ERROR("wordlist-download-error", "Could not download the wordlist from \"%url%\"")
                    }
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
                List<String> blockedWords = ChatFilter.getBlockedWords();
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

        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) return ChatFilter.getBlockedWords().stream().filter(s -> s.startsWith(args[2])).toList();

        return List.of();
    }
}
