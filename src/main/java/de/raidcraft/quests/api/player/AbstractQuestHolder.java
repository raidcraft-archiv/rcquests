package de.raidcraft.quests.api.player;

import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.util.CaseInsensitiveMap;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractQuestHolder implements QuestHolder {

    private final int id;
    private final String player;
    private final Map<String, Quest> allQuests = new CaseInsensitiveMap<>();

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
    public Quest getQuest(String name) throws QuestException {

        if (allQuests.containsKey(name)) {
            return allQuests.get(name);
        }
        ArrayList<Quest> foundQuests = new ArrayList<>();
        for (Quest quest : allQuests.values()) {
            if (!quest.isActive() || quest.isCompleted()) {
                continue;
            }
            if (quest.getFriendlyName().contains(name)) {
                foundQuests.add(quest);
            }
        }
        if (foundQuests.isEmpty()) {
            throw new QuestException("Du hast keine Quest mit dem Namen: " + name);
        }
        if (foundQuests.size() > 1) {
            throw new QuestException("Du hast mehrere Quests mit dem Namen " + name + ": " + StringUtils.join(foundQuests, ", "));
        }
        return foundQuests.get(0);
    }

    @Override
    public List<Quest> getAllQuests() {

        return new ArrayList<>(allQuests.values());
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

        allQuests.put(quest.getName(), quest);
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
