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

    int getId();

    UUID getPlayerId();

    Player getPlayer();

    boolean hasQuest(String quest);

    default boolean hasQuest(QuestTemplate template) {

        return hasQuest(template.getId());
    }

    boolean hasActiveQuest(String quest);

    default boolean hasActiveQuest(QuestTemplate template) {

        return hasActiveQuest(template.getId());
    }

    // TODO: do it better in AbstractQuestHolder class
    default boolean hasCompletedQuest(String questId) {
        for (Quest quest : getCompletedQuests()) {
            if (quest.getTemplate().getId().equals(questId)) {
                return true;
            }
        }
        return false;
    }

    default boolean hasCompletedQuest(QuestTemplate template) {
        return hasCompletedQuest(template.getId());
    }

    /**
     * Gets the given quest by its full unique displayName.
     * To find a quest use {@link de.raidcraft.quests.QuestManager#findQuest(QuestHolder, String)}
     *
     * @param quest to get
     *
     * @return quest or empty {@link java.util.Optional} if quest was not found
     */
    Optional<Quest> getQuest(String quest);

    /**
     * Gets the given quest of the unique displayName from the template
     *
     * @param questTemplate to get quest for
     * @return quest or empty {@link java.util.Optional} if quest was not found
     */
    Optional<Quest> getQuest(QuestTemplate questTemplate);

    List<Quest> getAllQuests();

    List<Quest> getCompletedQuests();

    List<Quest> getActiveQuests();

    QuestInventory getQuestInventory();

    default void sendMessage(String text) {

        if (text == null || text.equals("")) return;
        getPlayer().sendMessage(text);
    }

    void addQuest(Quest quest);

    void removeQuest(Quest quest);

    Quest startQuest(QuestTemplate template) throws QuestException;

    void save();

    /**
     * Unregisters all listeners of this {@link Player}.
     * Should be called when the player logs out or clears his cache.
     */
    void unregister();
}
