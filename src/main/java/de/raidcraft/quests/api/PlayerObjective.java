package de.raidcraft.quests.api;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface PlayerObjective extends TriggerListener {

    public Quest getQuest();

    public Objective getObjective();

    public Player getPlayer();
}
