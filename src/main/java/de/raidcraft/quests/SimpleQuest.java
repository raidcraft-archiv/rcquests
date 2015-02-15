package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.quests.api.ObjectiveTemplate;
import de.raidcraft.quests.api.PlayerObjective;
import de.raidcraft.quests.api.QuestHolder;
import de.raidcraft.quests.api.QuestTemplate;
import de.raidcraft.quests.api.impl.AbstractQuest;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.quests.tables.TPlayerObjective;
import de.raidcraft.quests.tables.TPlayerQuest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class SimpleQuest extends AbstractQuest {

    protected SimpleQuest(TPlayerQuest quest, QuestTemplate template, QuestHolder holder) {

        super(quest.getId(), template, holder);

        setPhase(quest.getPhase());
        setStartTime(quest.getStartTime());
        setCompletionTime(quest.getCompletionTime());
        updateObjectiveListeners();
    }

    @Override
    protected List<PlayerObjective> loadObjectives() {

        List<PlayerObjective> objectives = new ArrayList<>();
        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        for (ObjectiveTemplate objectiveTemplate : getTemplate().getObjectiveTemplates()) {
            TPlayerObjective entry = database.find(TPlayerObjective.class).where()
                    .eq("quest_id", getId())
                    .eq("objective_id", objectiveTemplate.getId()).findUnique();
            // create a new db entry if none exists
            if (entry == null) {
                entry = new TPlayerObjective();
                entry.setObjectiveId(objectiveTemplate.getId());
                entry.setQuest(database.find(TPlayerQuest.class, getId()));
                database.save(entry);
            }
            objectives.add(new SimplePlayerObjective(entry, this, objectiveTemplate));
        }
        return objectives;
    }

    @Override
    public void delete() {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayerQuest quest = database.find(TPlayerQuest.class, getId());
        if (quest != null) {
            database.delete(quest);
            if (isCompleted()) {
                TPlayer tPlayer = database.find(TPlayer.class).where().eq("player", getPlayer().getName()).findUnique();
                if (tPlayer != null && tPlayer.getCompletedQuests() > 0) {
                    tPlayer.setCompletedQuests(tPlayer.getCompletedQuests() - 1);
                    database.save(tPlayer);
                }
            }
            // also delete all requirements
            getTemplate().getRequirements().forEach(requirement -> requirement.delete(getPlayer()));
            getPlayerObjectives().forEach(objective -> objective.getObjectiveTemplate().getRequirements()
                    .forEach(requirement -> requirement.delete(getPlayer())));
        }
    }

    public void save() {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayerQuest quest = database.find(TPlayerQuest.class, getId());
        if (quest == null) return;
        quest.setPhase(getPhase());
        quest.setStartTime(getStartTime());
        quest.setCompletionTime(getCompletionTime());
        database.save(quest);
        // also save all quest objectives
        for (PlayerObjective objective : getPlayerObjectives()) {
            objective.save();
        }
        // and requirements
        getTemplate().getRequirements().forEach(Requirement::save);
    }
}
