package de.raidcraft.quests.api.player;

import de.raidcraft.quests.api.quest.objective.Objective;
import de.raidcraft.quests.api.quest.trigger.TriggerListener;
import de.raidcraft.quests.api.quest.Quest;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
public interface PlayerObjective extends TriggerListener {

    public int getId();

    public Quest getQuest();

    public Objective getObjective();

    public Player getPlayer();

    public Timestamp getCompletionTime();

    public boolean isCompleted();

    public void save();
}
