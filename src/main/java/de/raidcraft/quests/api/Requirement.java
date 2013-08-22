package de.raidcraft.quests.api;

import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface Requirement {

    public int getId();

    public String getType();

    public boolean isMet(Player player);
}
