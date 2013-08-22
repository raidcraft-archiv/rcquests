package de.raidcraft.quests;

import de.raidcraft.quests.api.AbstractTrigger;
import de.raidcraft.quests.api.Action;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public class SimpleTrigger extends AbstractTrigger {

    public SimpleTrigger(int id, ConfigurationSection data) {

        super(id, data);
    }

    @Override
    protected void loadActions(ConfigurationSection data) {

        if (data == null) {
            return;
        }
        List<Action> actions = new ArrayList<>();
        Set<String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                actions.add(new SimpleAction(Integer.parseInt(key), data.getConfigurationSection(key)));
            }
        }
        this.actions = actions.toArray(new Action[actions.size()]);
    }

    @Override
    public void inform(Player player) {
        //TODO: implement
    }
}
