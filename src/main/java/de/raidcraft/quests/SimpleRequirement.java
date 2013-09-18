package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.api.quest.requirement.AbstractRequirement;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class SimpleRequirement extends AbstractRequirement {

    private final ConfigurationSection data;

    public SimpleRequirement(int id, ConfigurationSection data) {

        super(id, data);
        this.data = data.getConfigurationSection("args");
    }

    @Override
    public boolean isMet(Player player) throws QuestException {

        return RaidCraft.getComponent(QuestManager.class).checkRequirement(getType(), player, data);
    }
}
