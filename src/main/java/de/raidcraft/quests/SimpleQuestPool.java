package de.raidcraft.quests;

import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.quests.api.QuestPool;
import de.raidcraft.util.MathUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SimpleQuestPool implements QuestPool {

    private File rootFolder;
    private List<String> questKeys = new ArrayList<>();
    private Map<String, QuestTemplate> cachedQuests = new HashMap<>();
    private QuestManager questManager;

    public SimpleQuestPool(File rootFolder, QuestManager questManager) {
        this.rootFolder = rootFolder;
        this.questManager = questManager;
    }


    @Override
    public Optional<QuestTemplate> getRandomQuest() {
        if (getPoolSize() == 0) {
            return Optional.empty();
        }
        int id = MathUtil.RANDOM.nextInt(getPoolSize());
        String questKey = questKeys.get(id);
        if (!cachedQuests.containsKey(questKey)) {
            // TODO: load Quest
        }
        return Optional.of(cachedQuests.get(questKey));
    }
    @Override
    public int getPoolSize() {
        return questKeys.size();
    }
}
