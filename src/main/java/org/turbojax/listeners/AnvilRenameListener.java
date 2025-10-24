package org.turbojax.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.turbojax.LogManager;
import org.turbojax.WordlistManager;
import org.turbojax.config.MainConfig;
import org.turbojax.config.Message;

public class AnvilRenameListener implements Listener {
    @EventHandler
    public void onAnvilRename(InventoryClickEvent event) {
        // Skipping non-anvil events
        if (event.getInventory().getType() != InventoryType.ANVIL) return;

        // Ignoring if the config says so
        if (MainConfig.handleAnvilRename().equalsIgnoreCase("ignore")) return;
        
        HumanEntity player = event.getWhoClicked();

        // Ignoring if the user has the bypass permission
        if (player.hasPermission("chatfilter.bypass") || player.hasPermission("chatfilter.bypass.anvil")) return;

        // Making sure the view is an AnvilView
        if (event.getView() instanceof AnvilView anvilView) {
            // Getting the new name of the item and any blocked words it contains
            String name = anvilView.getRenameText();
            HashMap<Integer,String> bannedWords = WordlistManager.getBannedWords(name);

            // Ignoring if there are no blocked words.
            if (bannedWords.size() == 0) return;
            
            // Getting the player and various placeholders
            Map<String,String> placeholders = new HashMap<>(Map.of("%player%", player.getName(), "%name%", name, "%word%", bannedWords.values().iterator().next()));

            // Cancelling the event if the config says so
            if (MainConfig.handleAnvilRename().equalsIgnoreCase("cancel")) {
                event.setCancelled(true);

                // Logging
                LogManager.log(Message.plaintext.serialize(Message.toComponent(Message.FILTER_ANVIL_CANCEL_LOG, placeholders)));

                placeholders.putAll(Message.getCommonPlaceholders());
                Bukkit.broadcast(Message.toComponent(Message.FILTER_ANVIL_CANCEL_MESSAGE, placeholders), "chatfilter.notify");
                Message.send(player, Message.FILTER_ANVIL_CANCEL_WARNING, placeholders);
            }

            // Censoring the name if the config says so
            if (MainConfig.handleAnvilRename().equalsIgnoreCase("censor")) {
                for (Entry<Integer,String> bannedWord : bannedWords.entrySet()) {
                    String newName = name.substring(0, bannedWord.getKey()) + "*".repeat(bannedWord.getValue().length()) + name.substring(bannedWord.getKey() + bannedWord.getValue().length());

                    // Renaming the output item
                    ItemStack output = anvilView.getItem(2);
                    output.editMeta(meta -> {
                        meta.displayName(Message.plaintext.deserialize(newName)); 
                    });

                    // Logging
                    placeholders.put("%new_name%", newName);
                    LogManager.log(Message.applyPlaceholders(Message.getMessage(Message.FILTER_ANVIL_CENSOR_LOG), placeholders));

                    placeholders.putAll(Message.getCommonPlaceholders());
                    Bukkit.broadcast(Message.toComponent(Message.FILTER_ANVIL_CENSOR_MESSAGE, placeholders), "chatfilter.notify");
                    Message.send(player, Message.FILTER_ANVIL_CENSOR_WARNING, placeholders);
                }
            }
        }
    }
}