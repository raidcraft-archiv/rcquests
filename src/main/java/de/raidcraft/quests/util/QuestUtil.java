package de.raidcraft.quests.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.quests.SimpleAction;
import de.raidcraft.quests.SimpleRequirement;
import de.raidcraft.quests.SimpleTrigger;
import de.raidcraft.quests.TriggerManager;
import de.raidcraft.quests.api.Action;
import de.raidcraft.quests.api.QuestTemplate;
import de.raidcraft.quests.api.Requirement;
import de.raidcraft.quests.api.Trigger;
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
                ConfigurationSection section = data.getConfigurationSection(key);
                if (section.getString("type").startsWith("this")) {
                    section.set("type", section.getString("type").replace("this", basePath));
                }
                SimpleRequirement requirement = new SimpleRequirement(Integer.parseInt(key), section);
                requirements.add(requirement.getId(), requirement);
            }
        }
        return requirements.toArray(new Requirement[requirements.size()]);
    }

    public static Trigger[] loadTriggers(ConfigurationSection data, QuestTemplate questTemplate) {

        if (data == null) {
            return new Trigger[0];
        }
        List<Trigger> triggers = new ArrayList<>();
        Set<String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                ConfigurationSection section = data.getConfigurationSection(key);
                if (section.getString("type").startsWith("this")) {
                    section.set("type", section.getString("type").replace("this", questTemplate.getBasePath()));
                }
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
                ConfigurationSection section = data.getConfigurationSection(key);
                if (section.getString("type").startsWith("this")) {
                    section.set("type", section.getString("type").replace("this", basePath));
                }
                actions.add(new SimpleAction<>(Integer.parseInt(key), provider, section));
            }
        }
        return actions;
    }
}
