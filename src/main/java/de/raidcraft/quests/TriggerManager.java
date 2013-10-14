package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.quests.QuestTrigger;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.api.quests.player.QuestHolder;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.api.quests.quest.trigger.Trigger;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public final class TriggerManager implements Component {

    private final QuestPlugin plugin;
    private final Map<String, List<Trigger>> loadedTriggers = new CaseInsensitiveMap<>();

    protected TriggerManager(QuestPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(TriggerManager.class, this);
    }

    public void registerTrigger(QuestTemplate questTemplate, Trigger... triggers) {

        for (Trigger trigger : triggers) {
            if (!loadedTriggers.containsKey(trigger.getName())) {
                loadedTriggers.put(trigger.getName(), new ArrayList<Trigger>());
            }
            loadedTriggers.get(trigger.getName()).add(trigger);
            // also load the counter part that actually trigger this
            Quests.initializeTrigger(questTemplate, trigger.getName(), trigger.getConfig());
        }
    }

    protected void callTrigger(QuestTrigger questTrigger, Player player) {

        if (!loadedTriggers.containsKey(questTrigger.getName())) {
            return;
        }
        QuestHolder holder = plugin.getQuestManager().getQuestHolder(player);
        for (Trigger trigger : loadedTriggers.get(questTrigger.getName())) {
            if (trigger.getQuestTemplate().equals(questTrigger.getQuestTemplate())) {
                trigger.trigger(holder);
            }
        }
    }
}
