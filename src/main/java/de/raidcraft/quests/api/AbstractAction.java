package de.raidcraft.quests.api;

import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public abstract class AbstractAction<T> implements Action<T> {

    private final int id;
    private final String name;
    private final T provider;

    public AbstractAction(int id, T provider, ConfigurationSection data) {

        this.id = id;
        this.name = data.getString("type");
        this.provider = provider;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public T getProvider() {

        return provider;
    }
}
