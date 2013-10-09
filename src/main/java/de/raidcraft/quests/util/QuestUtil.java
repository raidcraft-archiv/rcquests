package de.raidcraft.quests.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.mobs.Mobs;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.quests.SimpleAction;
import de.raidcraft.quests.SimpleRequirement;
import de.raidcraft.quests.SimpleTrigger;
import de.raidcraft.quests.TriggerManager;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.api.quest.action.Action;
import de.raidcraft.quests.api.quest.requirement.Requirement;
import de.raidcraft.quests.api.quest.trigger.Trigger;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Silthus
 */
public class QuestUtil {

    private final static Pattern pattern = Pattern.compile(".*#([\\w\\d\\s]+):([\\w\\d\\s]+)#.*");

    public static Requirement[] loadRequirements(ConfigurationSection data, String basePath) {

        if (data == null || data.getKeys(false) == null) {
            return new Requirement[0];
        }
        Set <String> keys = data.getKeys(false);
        List<Requirement> requirements = new ArrayList<>(keys.size());
        for (String key : keys) {
            try {
                if (key.equalsIgnoreCase("ordered")) {
                    continue;
                }
                ConfigurationSection section = replaceThisReferences(data.getConfigurationSection(key), basePath);
                SimpleRequirement requirement = new SimpleRequirement(Integer.parseInt(key), section);
                requirements.add(requirement.getId(), requirement);
            } catch (NumberFormatException e) {
                RaidCraft.LOGGER.warning("Wrong objective id in " + basePath + ": " + key);
            }
        }
        return requirements.toArray(new Requirement[requirements.size()]);
    }

    public static Trigger[] loadTrigger(ConfigurationSection data, QuestTemplate questTemplate) {

        if (data == null || data.getKeys(false) == null) {
            return new Trigger[0];
        }
        Set<String> keys = data.getKeys(false);
        List<Trigger> triggers = new ArrayList<>(keys.size());
        for (String key : keys) {
            try {
                ConfigurationSection section = replaceThisReferences(data.getConfigurationSection(key), questTemplate.getBasePath());
                triggers.add(new SimpleTrigger(Integer.parseInt(key), questTemplate, section));
            } catch (NumberFormatException e) {
                RaidCraft.LOGGER.warning("Wrong objective id in " + questTemplate.getId() + ": " + key);
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
            actions = new ArrayList<>(keys.size());
            for (String key : keys) {
                try {
                    if (key.equalsIgnoreCase("execute-once")) {
                        continue;
                    }
                    ConfigurationSection section = replaceThisReferences(data.getConfigurationSection(key), basePath);
                    actions.add(new SimpleAction<>(Integer.parseInt(key), provider, section));
                } catch (NumberFormatException e) {
                    RaidCraft.LOGGER.warning("Wrong objective id in " + basePath + ": " + key);
                }
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

    public static String replaceRefrences(String basePath, String value) {

        if (value == null || value.equals("")) {
            return value;
        }
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            String type = matcher.group(1);
            String name = matcher.group(2);
            if (matcher.group(2).contains("this")) {
                name = name.replace("this", basePath);
            }
            if (type.equalsIgnoreCase("mob")) {
                return Mobs.getFriendlyName(name);
            } else if (type.equalsIgnoreCase("host")) {
                return Quests.getFriendlyHostName(name);
            }
            return name;
        }
        return value;
    }
}
