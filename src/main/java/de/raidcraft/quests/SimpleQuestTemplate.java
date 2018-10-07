package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.objective.ObjectiveTemplate;
import de.raidcraft.quests.api.quest.AbstractQuestTemplate;
import de.raidcraft.quests.api.quest.Quest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.*;

/**
 * @author Silthus
 */
public class SimpleQuestTemplate extends AbstractQuestTemplate {

    protected SimpleQuestTemplate(String id, ConfigurationSection data) {

        super(id, data);
    }

    @Override
    public Class<Player> getTriggerEntityType() {

        return Player.class;
    }

    @Override
    public boolean processTrigger(Player player) {

        QuestHolder questHolder = RaidCraft.getComponent(QuestManager.class).getQuestHolder(player);
        Optional<Quest> quest = questHolder.getQuest(this);
        if (!quest.isPresent()) return true;
        // lets check if we already have a quest that is started
        // and do not execute actions if the quest is started
        if (quest.get().isActive()) {
            return false;
        }
        // if holder has completed the quest also pass the trigger
        if (quest.get().isCompleted()) {
            // lets check if the quest is repeatable and allow the trigger if the cooldown is over
            if (isRepeatable()
                    && quest.get().getCompletionTime().toInstant().plusSeconds(getCooldown()).isBefore(Instant.now())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Collection<ObjectiveTemplate> loadObjectives(ConfigurationSection data) {

        List<ObjectiveTemplate> objectiveTemplates = new ArrayList<>();
        if (data == null) return objectiveTemplates;
        Set<String> keys = data.getKeys(false);
        if (keys != null) {
            for (String key : keys) {
                try {
                    objectiveTemplates.add(new SimpleObjectiveTemplate(Integer.parseInt(key), this, data.getConfigurationSection(key)));
                } catch (NumberFormatException e) {
                    RaidCraft.LOGGER.warning(getId() + ": " + "Wrong objective id in " + getId() + ": " + key);
                }
            }
        }
        return objectiveTemplates;
    }

    @Override
    protected Collection<TriggerFactory> loadStartTrigger(ConfigurationSection data) {

        return ActionAPI.createTrigger(data);
    }

    @Override
    protected Collection<TriggerFactory> loadCompletionTrigger(ConfigurationSection data) {

        return ActionAPI.createTrigger(data);
    }

    @Override
    protected Collection<Requirement<Player>> loadRequirements(ConfigurationSection data) {

        return ActionAPI.createRequirements(getListenerId(), data, Player.class);
    }

    @Override
    protected Collection<Action<Player>> loadActions(ConfigurationSection data) {

        return ActionAPI.createActions(data, Player.class);
    }

    @Override
    protected Map<Quest.Phase, Map<String, String>> loadDefaultConversations(ConfigurationSection data) {
        HashMap<Quest.Phase, Map<String, String>> conversations = new HashMap<>();
        Arrays.stream(Quest.Phase.values()).forEach(phase -> conversations.put(phase, new HashMap<>()));

        ConfigurationSection defaultConvs = data.getConfigurationSection("default-convs");
        if (defaultConvs != null) {
            for (Quest.Phase phase : Quest.Phase.values()) {
                ConfigurationSection section = defaultConvs.getConfigurationSection(phase.getConfigName());
                if (section != null) {
                    for (String host : section.getKeys(false)) {
                        conversations.get(phase).put(host, section.getString(host));
                    }
                }
            }
        }

        return conversations;
    }
}
