package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.quests.api.Trigger;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * @author Silthus
 */
public final class TriggerManager implements Component {

    private final QuestPlugin plugin;
    private final Map<String, Trigger> loadedTriggers = new CaseInsensitiveMap<>();

    protected TriggerManager(QuestPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(TriggerManager.class, this);
    }

    public void registerTrigger(Trigger... triggers) {

        for (Trigger trigger : triggers) {
            loadedTriggers.put(trigger.getName(), trigger);
        }
    }

    protected void callTrigger(String name, Player player, ConfigurationSection data) {

        if (!loadedTriggers.containsKey(name)) {
            return;
        }
        loadedTriggers.get(name).inform(player, data);
    }
}
