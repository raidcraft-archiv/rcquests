package de.raidcraft.quests;

import de.raidcraft.quests.api.AbstractAction;
import de.raidcraft.quests.api.Quest;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author Silthus
 */
public class SimpleAction extends AbstractAction {

    public SimpleAction(int id, ConfigurationSection data) {

        super(id, data);
    }

    @Override
    public void execute(Quest quest) {
        //TODO: implement
    }
}
