package de.raidcraft.quests.api;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Silthus
 */
public interface Trigger {

    public int getId();

    public String getName();

    public ConfigurationSection getConfig();

    public QuestTemplate getQuestTemplate();

    public List<TriggerListener> getListeners();

    public void registerListener(TriggerListener listener);

    public void unregisterListener(TriggerListener listener);

    public List<Action<Trigger>> getActions();

    public void trigger(Player player);
}
