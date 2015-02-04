package de.raidcraft.quests.api;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class QuestHostInteractEvent extends Event {


    @Getter
    private final QuestHost questHost;
    @Getter
    private final Player player;

    public QuestHostInteractEvent(QuestHost questHost, Player player) {

        this.questHost = questHost;
        this.player = player;
    }

    // Bukkit stuff
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }

}
