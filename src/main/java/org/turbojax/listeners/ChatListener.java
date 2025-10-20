package org.turbojax.listeners;

import java.util.List;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.turbojax.WordlistManager;
import org.turbojax.config.MainConfig;

public class ChatListener implements Listener {
    public void onPlayerChat(PlayerChatEvent event) {
        if (MainConfig.handleChatMessage().equalsIgnoreCase("ignore")) return;

        String message = event.getMessage();
        List<String> foundWords = WordlistManager.getBannedWords(message);

        if (foundWords.isEmpty()) return;

        if (MainConfig.handleChatMessage().equalsIgnoreCase("delete")) event.setCancelled(true);
        if (MainConfig.handleChatMessage().equalsIgnoreCase("censor")) {
            for (String word : foundWords) {
                    message = message.replace(word, "*".repeat(word.length()));
                }
                event.setMessage(message);
        }
    }
}
