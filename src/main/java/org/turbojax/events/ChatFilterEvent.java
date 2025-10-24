package org.turbojax.events;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.turbojax.ChatFilterAction;

public class ChatFilterEvent extends Event implements Cancellable {
    private boolean cancelled = false;

    private ChatFilterAction action;
    private Player player;
    private String message;
    private List<String> blockedWords;

    public ChatFilterEvent(ChatFilterAction action, Player player, String message, List<String> blockedWords) {
        this.action = action;
        this.player = player;
        this.message = message;
        this.blockedWords = blockedWords;
    }
    /**
     * Using:
    ChatFilterEvent event = new ChatFilterEvent(action, player, name, blockedWords);
    Bukkit.getPluginManager().callEvent(event);
 
    if (event.isCancelled()) {
        // dostuff
    } else {
        // dostuff 2
    }
     */

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return new HandlerList();
    }

    public static HandlerList getHandlerList() {
        return new HandlerList();
    }
}