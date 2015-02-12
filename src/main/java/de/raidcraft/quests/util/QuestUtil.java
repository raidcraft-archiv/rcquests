package de.raidcraft.quests.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.mobs.Mobs;
import de.raidcraft.quests.QuestPlugin;
import de.raidcraft.quests.api.InvalidQuestHostException;
import de.raidcraft.quests.api.QuestException;
import org.bukkit.configuration.ConfigurationSection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QuestUtil {

    private final static Pattern pattern = Pattern.compile(".*#([\\w\\d\\s]+):([\\w\\d\\s]+)#.*");

    public static ConfigurationSection replacePathReferences(ConfigurationSection section, String basePath) {

        if (basePath.startsWith(".")) {
            basePath = basePath.replaceFirst("\\.", "");
        }
        for (String key : section.getKeys(true)) {
            if (section.isString(key)) {
                String value = section.getString(key);
                if (value.startsWith("this.")) {
                    value = value.replaceFirst("this", basePath);
                } else if (value.startsWith("..")) {
                    value = value.replaceFirst("\\.", basePath);
                }
                value = replaceRefrences(basePath, value);
                section.set(key, value);
            }
        }
        return section;
    }

    public static String replaceRefrences(String basePath, String value) {

        if (value == null || value.equals("")) {
            return value;
        }
        if (basePath.startsWith(".")) {
            basePath = basePath.replaceFirst("\\.", "");
        }
        Matcher matcher = pattern.matcher(value);
        if (matcher.matches()) {
            String type = matcher.group(1);
            String name = matcher.group(2);
            if (matcher.group(2).startsWith("this.")) {
                name = name.replaceFirst("this", basePath);
            } else if (matcher.group(2).startsWith("..")) {
                name = name.replaceFirst("\\.", basePath);
            }
            if (type.equalsIgnoreCase("mob")) {
                return Mobs.getFriendlyName(name);
            } else if (type.equalsIgnoreCase("host")) {
                try {
                    return RaidCraft.getComponent(QuestPlugin.class).getQuestManager().getQuestHost(name).getFriendlyName();
                } catch (InvalidQuestHostException e) {
                    RaidCraft.LOGGER.warning(e.getMessage());
                }
            } else if (type.equalsIgnoreCase("quest")) {
                try {
                    return RaidCraft.getComponent(QuestPlugin.class).getQuestManager().getQuestTemplate(name).getFriendlyName();
                } catch (QuestException e) {
                    RaidCraft.LOGGER.warning(e.getMessage());
                }
            }
            return name;
        }
        return value;
    }
}