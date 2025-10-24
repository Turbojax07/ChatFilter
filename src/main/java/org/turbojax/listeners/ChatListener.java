package org.turbojax.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.turbojax.LogManager;
import org.turbojax.WordlistManager;
import org.turbojax.config.MainConfig;
import org.turbojax.config.Message;

public class ChatListener implements Listener {
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        // Ignoring if the config says so
        if (MainConfig.handleChatMessage().equalsIgnoreCase("ignore")) return;

        Player player = event.getPlayer();

        // Ignoring if the user has the bypass permission
        if (player.hasPermission("chatfilter.bypass") || player.hasPermission("chatfilter.bypass.chat")) return;

        // Getting the message and any blocked words it contains
        String message = event.getMessage();
        HashMap<Integer,String> blockedWords = WordlistManager.getBannedWords(message);

        // Ignoring if there are no blocked words
        if (blockedWords.isEmpty()) return;

        Map<String,String> placeholders = new HashMap<>(Map.of("%player%", player.getName(), "%message%", message, "%word%", blockedWords.values().iterator().next()));

        // Cancelling the event if the config says so
        if (MainConfig.handleChatMessage().equalsIgnoreCase("cancel")) {
            event.setCancelled(true);

            // Logging
            LogManager.log(Message.plaintext.serialize(Message.toComponent(Message.FILTER_CHAT_CANCEL_LOG, placeholders)));

            placeholders.putAll(Message.getCommonPlaceholders());
            Bukkit.broadcast(Message.toComponent(Message.FILTER_CHAT_CANCEL_MESSAGE, placeholders), "chatfilter.notify");
            Message.send(player, Message.FILTER_CHAT_CANCEL_WARNING, placeholders);
        }

        // Censoring the message if the config says so
        if (MainConfig.handleChatMessage().equalsIgnoreCase("censor")) {
            for (Entry<Integer,String> blockedWord : blockedWords.entrySet()) {
                message = message.substring(0, blockedWord.getKey()) + "*".repeat(blockedWord.getValue().length()) + message.substring(blockedWord.getKey() + blockedWord.getValue().length());
            }

            player.chat(message);
            event.setCancelled(true);

            // Logging
            placeholders.put("%new_message%", message);
            LogManager.log(Message.plaintext.serialize(Message.toComponent(Message.FILTER_CHAT_CENSOR_LOG, placeholders)));

            placeholders.putAll(Message.getCommonPlaceholders());
            Bukkit.broadcast(Message.toComponent(Message.FILTER_CHAT_CENSOR_MESSAGE, placeholders), "chatfilter.notify");
            Message.send(player, Message.FILTER_CHAT_CENSOR_WARNING, placeholders);
        }
    }
}