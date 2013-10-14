package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.quest.AbstractQuestTemplate;
import de.raidcraft.api.quests.quest.objective.Objective;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.api.quests.quest.trigger.Trigger;
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

        this.requirements = QuestManager.loadRequirements(data, getBasePath());
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
                try {
                    objectives.add(new SimpleObjective(Integer.parseInt(key), this, data.getConfigurationSection(key)));
                } catch (NumberFormatException e) {
                    RaidCraft.LOGGER.warning("Wrong objective id in " + getId() + ": " + key);
                }
            }
        }
        this.objectives = objectives.toArray(new Objective[objectives.size()]);
    }

    @Override
    protected void loadTrigger(ConfigurationSection data) {

        this.trigger = QuestManager.loadTrigger(data, this, Trigger.Type.QUEST_START);
    }

    @Override
    protected void loadCompleteTrigger(ConfigurationSection data) {

        this.completeTrigger = QuestManager.loadTrigger(data, this, Trigger.Type.QUEST_ACCEPTED);
    }

    @Override
    protected void loadActions(ConfigurationSection data) {

        setActions(QuestManager.loadActions((QuestTemplate) this, data, getBasePath()));
    }
}
