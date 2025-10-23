package org.turbojax.listeners;

import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.turbojax.WordlistManager;
import org.turbojax.config.MainConfig;

public class ChatListener implements Listener {
    public void onPlayerChat(PlayerChatEvent event) {
        // Ignoring if the config says so
        if (MainConfig.handleChatMessage().equalsIgnoreCase("ignore")) {
            // FILTER_CHAT_IGNORE_LOG("filter-chat-ignore-log", "%prefix%You cannot have the word %word% in the name of your item.")
            // FILTER_CHAT_IGNORE_MESSAGE("filter-chat-ignore-message", "%prefix%You cannot have the word %word% in the name of your item.")
            // FILTER_CHAT_IGNORE_WARNING("filter-chat-ignore-warning", "%prefix%You cannot have the word %word% in the name of your item.")
            return;
        }

        // Getting the message and any blocked words it contains
        String message = event.getMessage();
        HashMap<Integer,String> blockedWords = WordlistManager.getBannedWords(message);

        // Ignoring if there are no blocked words
        if (blockedWords.isEmpty()) return;

        // Cancelling the event if the config says so
        if (MainConfig.handleChatMessage().equalsIgnoreCase("cancel")) {
            event.setCancelled(true);

            // FILTER_CHAT_CANCEL_LOG("filter-chat-cancel-log", "%prefix%You cannot have the word %word% in the name of your item.")
            // FILTER_CHAT_CANCEL_MESSAGE("filter-chat-cancel-message", "%prefix%.")
            // FILTER_CHAT_CANCEL_WARNING("filter-chat-cancel-warning")
        }

        // Censoring the message if the config says so
        if (MainConfig.handleChatMessage().equalsIgnoreCase("censor")) {
            for (Entry<Integer,String> blockedWord : blockedWords.entrySet()) {
                event.setMessage(message.substring(0, blockedWord.getKey()) + "*".repeat(blockedWord.getValue().length()) + message.substring(blockedWord.getKey() + blockedWord.getValue().length()));
            }

            // FILTER_CHAT_CENSOR_LOG("filter-chat-filter-log", "%prefix%You cannot have the word %word% in the name of your item.")
            // FILTER_CHAT_CENSOR_MESSAGE("filter-chat-filter-message", "%prefix%You cannot have the word %word% in the name of your item.")
            // FILTER_CHAT_CENSOR_WARNING("filter-chat-filter-warning")
        }
    }
}