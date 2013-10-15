package de.raidcraft.quests;

import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.api.quests.quest.objective.AbstractObjective;
import de.raidcraft.api.quests.quest.objective.Objective;
import de.raidcraft.api.quests.quest.trigger.Trigger;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class SimpleObjective extends AbstractObjective {

    protected SimpleObjective(int id, QuestTemplate questTemplate, ConfigurationSection data) {

        super(id, questTemplate, data);
    }

    @Override
    protected void loadRequirements(ConfigurationSection data) {

        this.requirements = QuestManager.loadRequirements(data, getQuestTemplate().getBasePath());
    }

    @Override
    protected void loadTrigger(ConfigurationSection data) {

        this.trigger = QuestManager.loadTrigger(data, getQuestTemplate(), Trigger.Type.QUEST_OBJECTIVE);
    }

    @Override
    protected void loadActions(ConfigurationSection data) {

        setActions(QuestManager.loadActions((Objective) this, data, getQuestTemplate().getBasePath()));
    }
}
