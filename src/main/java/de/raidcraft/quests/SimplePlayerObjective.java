package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.quests.api.objective.AbstractPlayerObjective;
import de.raidcraft.quests.api.objective.ObjectiveTemplate;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.quests.tables.TPlayerObjective;

/**
 * @author Silthus
 */
public class SimplePlayerObjective extends AbstractPlayerObjective {

    public SimplePlayerObjective(TPlayerObjective tableEntry, Quest quest, ObjectiveTemplate objectiveTemplate) {

        super(tableEntry.getId(), quest, objectiveTemplate);
        setCompletionTime(tableEntry.getCompletionTime());
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayerObjective objective = database.find(TPlayerObjective.class, getId());
        objective.setCompletionTime(getCompletionTime());
        database.save(objective);
        // save all requirements
        getObjectiveTemplate().getRequirements().forEach(Requirement::save);
    }
}
