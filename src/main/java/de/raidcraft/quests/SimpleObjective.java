package de.raidcraft.quests;

import de.raidcraft.quests.api.quest.objective.AbstractObjective;
import de.raidcraft.quests.api.quest.objective.Objective;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.api.quest.trigger.Trigger;
import de.raidcraft.quests.util.QuestUtil;
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

        this.requirements = QuestUtil.loadRequirements(data, getQuestTemplate().getBasePath());
    }

    @Override
    protected void loadTrigger(ConfigurationSection data) {

        this.trigger = QuestUtil.loadTrigger(data, getQuestTemplate(), Trigger.Type.QUEST_OBJECTIVE);
    }

    @Override
    protected void loadActions(ConfigurationSection data) {

        setActions(QuestUtil.loadActions((Objective) this, data, getQuestTemplate().getBasePath()));
    }
}
