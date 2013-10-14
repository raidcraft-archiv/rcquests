package de.raidcraft.quests.api.quest.trigger;

import de.raidcraft.quests.api.player.QuestHolder;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.api.quest.action.Action;
import org.bukkit.configuration.ConfigurationSection;

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

    public void trigger(QuestHolder holder);
}
