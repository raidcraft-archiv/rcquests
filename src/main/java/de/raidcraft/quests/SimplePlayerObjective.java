package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.quests.api.AbstractPlayerObjective;
import de.raidcraft.quests.api.Objective;
import de.raidcraft.quests.api.Quest;
import de.raidcraft.quests.api.Requirement;
import de.raidcraft.quests.tables.TPlayerObjective;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
public class SimplePlayerObjective extends AbstractPlayerObjective {

    public SimplePlayerObjective(int id, Quest quest, Objective objective) {

        super(id, quest, objective);
    }

    @Override
    public void trigger(Player player) {

        if (!getPlayer().equals(player) || isCompleted()) {
            return;
        }
        boolean complete = true;
        for (Requirement requirement : getObjective().getRequirements()) {
            if (!requirement.isMet(player)) {
                complete = false;
                break;
            }
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
    }
}
