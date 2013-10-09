package de.raidcraft.quests.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.quests.SimpleAction;
import de.raidcraft.quests.SimpleRequirement;
import de.raidcraft.quests.SimpleTrigger;
import de.raidcraft.quests.TriggerManager;
import de.raidcraft.quests.api.quest.action.Action;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.api.quest.requirement.Requirement;
import de.raidcraft.quests.api.quest.trigger.Trigger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public class QuestUtil {

    public static Requirement[] loadRequirements(ConfigurationSection data, String basePath) {

        if (data == null) {
            return new Requirement[0];
        }
        List<Requirement> requirements = new ArrayList<>();
        Set <String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                ConfigurationSection section = replaceThisReferences(data.getConfigurationSection(key), basePath);
                SimpleRequirement requirement = new SimpleRequirement(Integer.parseInt(key), section);
                requirements.add(requirement.getId(), requirement);
            }
        }
        return requirements.toArray(new Requirement[requirements.size()]);
    }

    public static Trigger[] loadTrigger(ConfigurationSection data, QuestTemplate questTemplate) {

        if (data == null) {
            return new Trigger[0];
        }
        List<Trigger> triggers = new ArrayList<>();
        Set<String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                ConfigurationSection section = replaceThisReferences(data.getConfigurationSection(key), questTemplate.getBasePath());
                triggers.add(new SimpleTrigger(Integer.parseInt(key), questTemplate, section));
            }
        }
        Trigger[] loadedTriggers = triggers.toArray(new Trigger[triggers.size()]);
        RaidCraft.getComponent(TriggerManager.class).registerTrigger(loadedTriggers);
        return loadedTriggers;
    }

    public static <T> List<Action<T>> loadActions(T provider, ConfigurationSection data, String basePath) {

        ArrayList<Action<T>> actions = new ArrayList<>();
        if (data == null) {
            return actions;
        }
        Set<String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                ConfigurationSection section = replaceThisReferences(data.getConfigurationSection(key), basePath);
                actions.add(new SimpleAction<>(Integer.parseInt(key), provider, section));
            }
        }
        return actions;
    }

    private static ConfigurationSection replaceThisReferences(ConfigurationSection section, String basePath) {

        for (String key : section.getKeys(true)) {
            if (section.getString(key).startsWith("this")) {
                section.set(key, section.getString(key).replace("this", basePath));
            }
        }
        return section;
    }
}
