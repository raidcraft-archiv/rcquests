package de.raidcraft.quests.api;

import de.raidcraft.api.quests.QuestException;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface Requirement {

    public int getId();

    public String getType();

    public boolean isMet(Player player) throws QuestException;
}
