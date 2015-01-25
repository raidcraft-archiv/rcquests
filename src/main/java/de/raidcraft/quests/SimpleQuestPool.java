package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.quests.api.QuestPool;
import de.raidcraft.util.MathUtil;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SimpleQuestPool implements QuestPool {

    private File rootFolder;
    private List<String> questKeys = new ArrayList<>();
    private Map<String, QuestTemplate> loadedQuests = new HashMap<>();
    private QuestPlugin plugin;
    private QuestManager questManager;
    private String type;

    public SimpleQuestPool(File rootFolder, String type) {
        this.rootFolder = rootFolder;
        this.type = type;
        reload();
    }

    @Override
    public void reload() {
        this.questKeys.clear();
        loadedQuests.clear();
        this.plugin = RaidCraft.getComponent(QuestPlugin.class);
        this.questManager = this.plugin.getQuestManager();
        rootFolder.mkdirs();
        loadQuestConfigs(rootFolder, type);
        plugin.info("QuestPool." + type + ":" + questKeys.size() + " TimeQuests loaded.");
    }

    // TODO: shared code with QuestManager
    private void loadQuestConfigs(File baseFolder, String path) {

        for (File file : baseFolder.listFiles()) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                loadQuestConfigs(file, path + "." + fileName.toLowerCase());
                continue;
            }
            String id = path + "." + file.getName().toLowerCase();
            ConfigurationSection config = plugin.configure(new SimpleConfiguration<>(plugin, file));

            SimpleQuestTemplate quest = new SimpleQuestTemplate(id, config);
            // lets register the triggers of the quest
            quest.registerListeners();
            questKeys.add(id);
            loadedQuests.put(id, quest);
            plugin.info("Loaded TimeQuest: " + id + " - " + quest.getFriendlyName());
        }
    }

    @Override
    public Optional<QuestTemplate> getRandomQuest() {
        if (getPoolSize() == 0) {
            return Optional.empty();
        }
        int id = MathUtil.RANDOM.nextInt(getPoolSize());
        String questKey = questKeys.get(id);
        return Optional.of(loadedQuests.get(questKey));
    }
    @Override
    public int getPoolSize() {
        return questKeys.size();
    }
}
