package de.raidcraft.quests;

import de.raidcraft.quests.api.quest.AbstractQuestTemplate;
import de.raidcraft.quests.api.quest.objective.Objective;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.util.QuestUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public class SimpleQuestTemplate extends AbstractQuestTemplate {

    protected SimpleQuestTemplate(String id, ConfigurationSection data) {

        super(id, data);
    }

    @Override
    protected void loadRequirements(ConfigurationSection data) {

        this.requirements = QuestUtil.loadRequirements(data, getBasePath());
    }

    @Override
    protected void loadObjectives(ConfigurationSection data) {

        if (data == null) {
            return;
        }
        List<Objective> objectives = new ArrayList<>();
        Set <String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                objectives.add(new SimpleObjective(Integer.parseInt(key), this, data.getConfigurationSection(key)));
            }
        }
        this.objectives = objectives.toArray(new Objective[objectives.size()]);
    }

    @Override
    protected void loadTrigger(ConfigurationSection data) {

        this.trigger = QuestUtil.loadTrigger(data, this);
    }

    @Override
    protected void loadCompleteTrigger(ConfigurationSection data) {

        this.completeTrigger = QuestUtil.loadTrigger(data, this);
    }

    @Override
    protected void loadActions(ConfigurationSection data) {

        setActions(QuestUtil.loadActions((QuestTemplate) this, data, getBasePath()));
    }
}
