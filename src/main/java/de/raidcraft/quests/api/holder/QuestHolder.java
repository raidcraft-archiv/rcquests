package de.raidcraft.quests.api.holder;

import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.ui.QuestInventory;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
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

    /**
     * Gets the given quest by its full unique name.
     * To find a quest use {@link de.raidcraft.quests.QuestManager#findQuest(QuestHolder, String)}
     *
     * @param quest to get
     *
     * @return quest or empty {@link java.util.Optional} if quest was not found
     */
    public Optional<Quest> getQuest(String quest);

    /**
     * Gets the given quest of the unique name from the template
     *
     * @param questTemplate to get quest for
     * @return quest or empty {@link java.util.Optional} if quest was not found
     */
    public Optional<Quest> getQuest(QuestTemplate questTemplate);

    public List<Quest> getAllQuests();

    public List<Quest> getCompletedQuests();

    public List<Quest> getActiveQuests();

    public QuestInventory getQuestInventory();

    public default void sendMessage(String text) {

        if (text == null || text.equals("")) return;
        getPlayer().sendMessage(text);
    }

    public void addQuest(Quest quest);

    public void removeQuest(Quest quest);

    public Quest startQuest(QuestTemplate template) throws QuestException;

    public void save();
}
