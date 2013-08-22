package de.raidcraft.quests.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractQuestHolder implements QuestHolder {

    private final int id;
    private final String player;
    private final List<Quest> allQuests = new ArrayList<>();

    public AbstractQuestHolder(int id, String player) {

        this.id = id;
        this.player = player;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return player;
    }

    @Override
    public Player getPlayer() {

        return Bukkit.getPlayer(getName());
    }

    @Override
    public List<Quest> getAllQuests() {

        return allQuests;
    }

    @Override
    public List<Quest> getCompletedQuests() {

        ArrayList<Quest> completedQuests = new ArrayList<>();
        for (Quest quest : getAllQuests()) {
            if (quest.isCompleted()) {
                completedQuests.add(quest);
            }
        }
        return completedQuests;
    }

    @Override
    public List<Quest> getActiveQuests() {

        ArrayList<Quest> activeQuests = new ArrayList<>();
        for (Quest quest : getAllQuests()) {
            if (quest.isActive()) {
                activeQuests.add(quest);
            }
        }
        return activeQuests;
    }

    @Override
    public void addQuest(Quest quest) {

        allQuests.add(quest);
    }

    @Override
    public void abortQuest(Quest quest) {

        quest.abort();
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof AbstractQuestHolder)) return false;

        AbstractQuestHolder that = (AbstractQuestHolder) o;

        return player.equals(that.player);
    }

    @Override
    public int hashCode() {

        return player.hashCode();
    }

    @Override
    public String toString() {

        return player;
    }
}
