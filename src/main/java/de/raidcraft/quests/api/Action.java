package de.raidcraft.quests.api;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface Action<T> {

    public int getId();

    public String getType();

    public T getProvider();

    public void execute(Player player, T holder);
}
