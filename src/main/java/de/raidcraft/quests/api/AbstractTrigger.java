package de.raidcraft.quests.api;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public abstract class AbstractTrigger implements Trigger {

    private final int id;
    private final String type;
    private final QuestTemplate questTemplate;
    private final List<TriggerListener> listeners = new ArrayList<>();
    private List<Action<Trigger>> actions = new ArrayList<>();

    public AbstractTrigger(int id, QuestTemplate questTemplate, ConfigurationSection data) {

        this.id = id;
        this.type = data.getString("type");
        this.questTemplate = questTemplate;
        loadActions(data.getConfigurationSection("actions"));
    }

    protected abstract void loadActions(ConfigurationSection data);

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getType() {

        return type;
    }

    @Override
    public QuestTemplate getQuestTemplate() {

        return questTemplate;
    }

    @Override
    public List<Action<Trigger>> getActions() {

        return actions;
    }

    protected void setActions(List<Action<Trigger>> actions) {

        this.actions = actions;
    }

    @Override
    public List<TriggerListener> getListeners() {

        return listeners;
    }

    @Override
    public void registerListener(TriggerListener listener) {

        listeners.add(listener);
    }

    @Override
    public void unregisterListener(TriggerListener listener) {

        listeners.remove(listener);
    }
}
