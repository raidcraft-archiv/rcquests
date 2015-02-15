package de.raidcraft.quests.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.quests.QuestManager;
import de.raidcraft.quests.api.QuestConfigLoader;
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
                String value = replacePathReference(section.getString(key), basePath);
                value = replaceRefrences(basePath, value);
                section.set(key, value);
            }
        }
        return section;
    }

    public static String replacePathReference(String value, String basePath) {

        if (value.startsWith("this.")) {
            value = value.replaceFirst("this", basePath);
        } else if (value.startsWith("../")) {
            String[] sections = basePath.split("\\.");
            basePath = "";
            for (int i = sections.length; i >= 0; --i) {
                if (value.startsWith("../")) {
                    value = value.replace("\\.\\./", "");
                } else {
                    basePath = sections[i] + "." + basePath;
                }
            }
            value = basePath + value;
        }
        return value;
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
            String name = replacePathReference(matcher.group(2), basePath);
            QuestConfigLoader loader = RaidCraft.getComponent(QuestManager.class).getQuestConfigLoader(type);
            if (loader != null) {
                try {
                    return loader.replaceReference(name);
                } catch (UnsupportedOperationException e) {
                    RaidCraft.LOGGER.warning("The Quest Config loader " + loader.getSuffix() + " does not support reference replacements!");
                }
            }
        }
        return value;
    }
}