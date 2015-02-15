package de.raidcraft.quests.api;


public interface TimeQuestManager {

    public static final String TYPE_DAILY = "daily";

    public boolean checkTimeQuests(String type, QuestHolder holder);

    public void reload();
}
