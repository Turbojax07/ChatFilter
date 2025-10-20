package org.turbojax.listeners;

import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.view.AnvilView;

public class AnvilRenameListener implements Listener {
    public void onAnvilRename(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) return;

        if (event.getView() instanceof AnvilView anvilView) {
            anvilView.getRenameText()
        }
    }
}
