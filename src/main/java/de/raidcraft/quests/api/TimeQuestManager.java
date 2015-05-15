package de.raidcraft.quests.api;


import de.raidcraft.quests.api.holder.QuestHolder;

public interface TimeQuestManager {

    String TYPE_DAILY = "daily";

    boolean checkTimeQuests(String type, QuestHolder holder);

    void reload();
}
