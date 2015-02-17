package de.raidcraft.quests.api.quest;

import java.util.Optional;

/**
 * It is not possible to get all quests.
 * Because the quests are loaded dynamicly.
 * @author IDragonfire
 */
public interface QuestPool {

    public Optional<QuestTemplate> getRandomQuest();

    public int getPoolSize();

    public void reload();
}