package org.turbojax.listeners;

import java.util.HashMap;
import java.util.Map.Entry;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.view.AnvilView;
import org.turbojax.WordlistManager;
import org.turbojax.config.MainConfig;
import org.turbojax.config.Messages;

public class AnvilRenameListener implements Listener {
    public void onAnvilRename(InventoryClickEvent event) {
        // Skipping non-anvil events
        if (event.getInventory().getType() != InventoryType.ANVIL) return;

        // Ignoring if the config says so
        if (MainConfig.handleAnvilRename().equalsIgnoreCase("ignore")) {
            // FILTER_ANVIL_IGNORE_LOG("filter-anvil-ignore-log", "%prefix%You cannot have the word %word% in the name of your item.")
            // FILTER_ANVIL_IGNORE_MESSAGE("filter-anvil-ignore-message", "%prefix%You cannot have the word %word% in the name of your item.")
            // FILTER_ANVIL_IGNORE_WARNING("filter-anvil-ignore-warning", "%prefix%You cannot have the word %word% in the name of your item.")
            return;
        }

        // Making sure the view is an AnvilView
        if (event.getView() instanceof AnvilView anvilView) {
            // Getting the new name of the item and any blocked words it contains
            String name = anvilView.getRenameText();
            HashMap<Integer,String> bannedWords = WordlistManager.getBannedWords(name);

            // Ignoring if there are no blocked words.
            if (bannedWords.size() == 0) return;
            
            // Cancelling the event if the config says so
            if (MainConfig.handleAnvilRename().equalsIgnoreCase("cancel")) {
                event.setCancelled(true);

                // Renaming the output item
                ItemStack output = anvilView.getItem(2);
                output.editMeta(meta -> {
                   meta.displayName(Messages.plaintext.deserialize(MainConfig.anvilDefaultName()));
                });

                // FILTER_ANVIL_CANCEL_LOG("filter-anvil-cancel-log", "%prefix%You cannot have the word %word% in the name of your item.")
                // FILTER_ANVIL_CANCEL_MESSAGE("filter-anvil-cancel-message", "%prefix%.")
                // FILTER_ANVIL_CANCEL_WARNING("filter-anvil-cancel-warning")
            }

            // Censoring the name if the config says so
            for (Entry<Integer,String> bannedWord : bannedWords.entrySet()) {
                // Renaming the output item
                ItemStack output = anvilView.getItem(2);
                output.editMeta(meta -> {
                   meta.displayName(Messages.plaintext.deserialize(name.substring(0, bannedWord.getKey()) + "*".repeat(bannedWord.getValue().length()) + name.substring(bannedWord.getKey() + bannedWord.getValue().length()))); 
                });

                // FILTER_ANVIL_CENSOR_LOG("filter-anvil-filter-log", "%prefix%You cannot have the word %word% in the name of your item.")
                // FILTER_ANVIL_CENSOR_MESSAGE("filter-anvil-filter-message", "%prefix%You cannot have the word %word% in the name of your item.")
                // FILTER_ANVIL_CENSOR_WARNING("filter-anvil-filter-warning")
            }
        }
    }
}