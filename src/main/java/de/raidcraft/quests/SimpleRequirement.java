package de.raidcraft.quests;

import de.raidcraft.quests.api.AbstractRequirement;
import de.raidcraft.quests.api.Quest;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class SimpleRequirement extends AbstractRequirement {

    public SimpleRequirement(int id, ConfigurationSection data) {

        super(id, data);
    }

    @Override
    public boolean isMet(Quest quest) {

        // TODO
        return true;
    }
}
