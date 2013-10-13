package de.raidcraft.quests.api.quest.trigger;

import de.raidcraft.quests.api.quest.action.Action;
import de.raidcraft.quests.api.quest.QuestTemplate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Silthus
 */
public interface Trigger {

    public enum Type {

        QUEST_START,
        QUEST_OBJECTIVE,
        QUEST_ACCEPTED
    }

    public int getId();

    public String getName();

    public Type getType();

    public long getDelay();

    public ConfigurationSection getConfig();

    public QuestTemplate getQuestTemplate();

    public List<TriggerListener> getListeners();

    public void registerListener(TriggerListener listener);

    public void unregisterListener(TriggerListener listener);

    public List<Action<Trigger>> getActions();

    public void trigger(Player player);
}
