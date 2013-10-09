package de.raidcraft.quests.api.quest.action;

import de.raidcraft.api.quests.QuestException;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public interface Action<T> {

    public int getId();

    public String getName();

    public T getProvider();

    public boolean isExecuteOnce();

    public long getDelay();

    public long getCooldown();

    public void execute(Player player, T holder) throws QuestException;

    public void save();
}
