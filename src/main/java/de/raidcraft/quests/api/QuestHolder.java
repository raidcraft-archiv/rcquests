package de.raidcraft.quests.api;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Silthus
 */
public interface QuestHolder {

    public String getName();

    public Player getPlayer();

    public List<Quest> getAllQuests();

    public List<Quest> getCompletedQuests();

    public List<Quest> getActiveQuests();
}
