package de.raidcraft.quests.api;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface Trigger {

    public int getId();

    public String getName();

    public Action[] getActions();

    public void inform(Player player);
}
