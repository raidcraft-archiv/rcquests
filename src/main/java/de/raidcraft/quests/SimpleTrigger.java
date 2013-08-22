package de.raidcraft.quests;

import de.raidcraft.quests.api.AbstractTrigger;
import de.raidcraft.quests.api.Action;
import de.raidcraft.quests.api.QuestTemplate;
import de.raidcraft.quests.api.Trigger;
import de.raidcraft.quests.api.TriggerListener;
import de.raidcraft.quests.util.QuestUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class SimpleTrigger extends AbstractTrigger {

    private final ConfigurationSection data;

    public SimpleTrigger(int id, QuestTemplate questTemplate, ConfigurationSection data) {

        super(id, questTemplate, data);
        this.data = data;
    }

    @Override
    protected void loadActions(ConfigurationSection data) {

        setActions(QuestUtil.loadActions((Trigger) this, data, getQuestTemplate().getBasePath()));
    }

    @Override
    public void inform(Player player, ConfigurationSection data) {

        // we need to check if our information matches the given information
        for (String key : data.getKeys(false)) {
            if (key.equals("type") || key.equals("actions")) {
                continue;
            }
            Object sourceValue = this.data.get(key);
            Object targetValue = data.get(key);
            if (sourceValue == null || targetValue == null || !sourceValue.equals(targetValue)) {
                return;
            }
        }
        // we have a match now lets execute our actions
        for (Action<Trigger> action : getActions()) {
            action.execute(player, this);
        }
        // and inform our listeners
        for (TriggerListener listener : getListeners()) {
            listener.trigger(player);
        }
    }
}
