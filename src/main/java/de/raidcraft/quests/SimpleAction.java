package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.quests.api.AbstractAction;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class SimpleAction<T> extends AbstractAction<T> {

    private final ConfigurationSection data;

    public SimpleAction(int id, T provider, ConfigurationSection data) {

        super(id, provider, data);
        this.data = data;
    }

    @Override
    public void execute(Player player, T holder) {

        RaidCraft.getComponent(QuestManager.class).executeAction(getName(), player, data);
    }
}
