package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.player.AbstractPlayerObjective;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.Quest;
import de.raidcraft.api.quests.quest.action.Action;
import de.raidcraft.api.quests.quest.objective.Objective;
import de.raidcraft.api.quests.quest.requirement.Requirement;
import de.raidcraft.quests.tables.TPlayerObjective;
import org.bukkit.ChatColor;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
public class SimplePlayerObjective extends AbstractPlayerObjective {

    public SimplePlayerObjective(int id, Quest quest, Objective objective) {

        super(id, quest, objective);
    }

    @Override
    public void trigger(QuestHolder questHolder) {

        if (!getPlayer().equals(questHolder) || isCompleted()) {
            return;
        }
        boolean complete = true;
        try {
            for (Requirement requirement : getObjective().getRequirements()) {
                if (!requirement.isMet(questHolder.getPlayer())) {
                    complete = false;
                    break;
                }
            }
        } catch (QuestException e) {
            questHolder.getPlayer().sendMessage(ChatColor.RED + e.getMessage());
        }
        if (complete) {
            setCompleted(new Timestamp(System.currentTimeMillis()));
            getQuest().completeObjective(this);
        }
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayerObjective objective = database.find(TPlayerObjective.class, getId());
        objective.setCompletionTime(getCompletionTime());
        database.save(objective);
        // save actions
        for (Action<Objective> action : getObjective().getActions()) {
            action.save();
        }
    }
}
