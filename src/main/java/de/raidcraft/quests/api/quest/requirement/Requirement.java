package de.raidcraft.quests.api.quest.requirement;

import de.raidcraft.api.quests.QuestException;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface Requirement extends Comparable<Requirement> {

    public int getId();

    public String getType();

    public boolean isMet(Player player) throws QuestException;
}
