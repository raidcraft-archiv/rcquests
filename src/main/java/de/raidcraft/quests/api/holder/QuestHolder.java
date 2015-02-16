package de.raidcraft.quests.api.holder;

import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.quests.api.quest.QuestTemplate;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * @author Silthus
 */
public interface QuestHolder {

    public int getId();

    public UUID getPlayerId();

    public Player getPlayer();

    public boolean hasQuest(String quest);

    public default boolean hasQuest(QuestTemplate template) {

        return hasQuest(template.getId());
    }

    public boolean hasActiveQuest(String quest);

    public default boolean hasActiveQuest(QuestTemplate template) {

        return hasActiveQuest(template.getId());
    }

    // TODO: do it better in AbstractQuestHolder class
    public default boolean hasCompletedQuest(String questId) {
        for (Quest quest : getCompletedQuests()) {
            if (quest.getTemplate().getId().equals(questId)) {
                return true;
            }
        }
        return false;
    }

    public default boolean hasCompletedQuest(QuestTemplate template) {
        return hasCompletedQuest(template.getId());
    }

    public Quest getQuest(String quest) throws QuestException;

    @Nullable
    public Quest getQuest(QuestTemplate questTemplate);

    public List<Quest> getAllQuests();

    public List<Quest> getCompletedQuests();

    public List<Quest> getActiveQuests();

    public default void sendMessage(String text) {

        if (text == null || text.equals("")) return;
        getPlayer().sendMessage(text);
    }

    public void addQuest(Quest quest);

    public void abortQuest(Quest quest);

    public Quest createQuest(QuestTemplate template);

    public Quest startQuest(QuestTemplate template) throws QuestException;

    public void save();
}
