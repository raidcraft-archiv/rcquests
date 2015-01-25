package de.raidcraft.quests.api;

import de.raidcraft.api.quests.holder.QuestHolder;

public interface TimeQuestManager {

    public static final String TYPE_DAILY = "daily";

    public boolean checkTimeQuests(String type, QuestHolder holder);

    public void reload();
}
