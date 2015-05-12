package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionException;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.random.GenericRDSTable;
import de.raidcraft.api.random.RDSObject;
import de.raidcraft.quests.api.events.QuestPoolQuestCompletedEvent;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.random.RDSQuestObject;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.quests.tables.TPlayerQuest;
import de.raidcraft.quests.tables.TPlayerQuestPool;
import de.raidcraft.util.ConfigUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestPool extends GenericRDSTable implements Listener {

    private final boolean enabled;
    private final String name;
    private final String friendlyName;
    private final int maxActiveQuests;
    private final double cooldown;
    private final boolean abortPrevious;
    private final Optional<LocalTime> resetTime;
    private final List<Action<Player>> rewardActions = new ArrayList<>();

    protected QuestPool(String name, ConfigurationSection config) {

        super(null, config.getInt("count", 1), config.getDouble("probability", 1.0));
        this.name = name;
        this.enabled = config.getBoolean("enabled", true);
        this.friendlyName = config.isSet("name") ? config.getString("name") : name;
        this.maxActiveQuests = config.getInt("max-active-quests", 1);
        this.abortPrevious = config.getBoolean("abort-previous-quests", false);
        // 24h
        this.cooldown = config.getDouble("cooldown", 86400);
        if (config.isSet("reset-time")) {
            this.resetTime = Optional.ofNullable(LocalTime.parse(config.getString("reset-time"), DateTimeFormatter.ISO_TIME));
        } else {
            this.resetTime = Optional.empty();
        }

        ConfigurationSection quests = config.getConfigurationSection("quests");
        if (quests != null && quests.getKeys(false) != null) {
            QuestManager questManager = RaidCraft.getComponent(QuestManager.class);
            for (String key : quests.getKeys(false)) {
                try {
                    QuestTemplate questTemplate = questManager.getQuestTemplate(key);
                    if (questTemplate.isRepeatable()) {
                        addEntry(new RDSQuestObject(questTemplate));
                    } else {
                        RaidCraft.LOGGER.warning("Can only add repeatable quests to a quest pool: " + key + " in " + ConfigUtil.getFileName(config));
                    }
                } catch (QuestException e) {
                    RaidCraft.LOGGER.warning("Quest " + key + " in quest pool not found! " + ConfigUtil.getFileName(config));
                }
            }
        }

        try {
            rewardActions.addAll(ActionFactory.getInstance().createActions(config.getConfigurationSection("actions"), Player.class));
        } catch (ActionException e) {
            e.printStackTrace();
        }
    }

    public Optional<TPlayerQuestPool> getDatabaseEntry(QuestHolder questHolder) {

        return Optional.ofNullable(RaidCraft.getDatabase(QuestPlugin.class).find(TPlayerQuestPool.class).where()
                .eq("player_id", questHolder.getId())
                .eq("quest_pool", getName())
                .findUnique());
    }

    public List<TPlayerQuest> getActiveQuests(TPlayerQuestPool pool) {

        return pool.getQuests().stream()
                .filter(tPlayerQuest -> tPlayerQuest.getCompletionTime() == null
                        && tPlayerQuest.getStartTime() != null)
                .collect(Collectors.toList());
    }

    public List<QuestTemplate> getResult(QuestHolder questHolder) {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayerQuestPool dbQuestPool;
        Optional<TPlayerQuestPool> databaseEntry = getDatabaseEntry(questHolder);
        if (databaseEntry.isPresent()) {
            dbQuestPool = databaseEntry.get();
        } else {
            dbQuestPool = new TPlayerQuestPool();
            dbQuestPool.setPlayer(database.find(TPlayer.class, questHolder.getId()));
            dbQuestPool.setQuestPool(getName());
            database.save(dbQuestPool);
        }

        List<TPlayerQuest> activeQuests = getActiveQuests(dbQuestPool);

        if (activeQuests.size() < maxActiveQuests) {
            if (getCount() > maxActiveQuests - activeQuests.size()) setCount(maxActiveQuests - activeQuests.size());
            Collection<RDSObject> result = super.getResult();
            List<QuestTemplate> queriedQuests = result.stream()
                    .filter(object -> object instanceof RDSQuestObject)
                    .map(object -> (RDSQuestObject) object)
                    .filter(rdsQuestObject -> rdsQuestObject.getValue().isPresent())
                    .map(rdsQuestObject -> rdsQuestObject.getValue().get())
                    .collect(Collectors.toList());
            // lets filter out quests that are already active
            List<String> questNames = activeQuests.stream().map(TPlayerQuest::getQuest).collect(Collectors.toList());
            return queriedQuests.stream()
                    .filter(questTemplate -> !questNames.contains(questTemplate.getId()))
                    .filter(questTemplate -> !questHolder.hasActiveQuest(questTemplate))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @EventHandler(ignoreCancelled = true)
    public void onQuestCompleted(QuestPoolQuestCompletedEvent event) {

        rewardActions.forEach(playerAction -> playerAction.accept(event.getQuest().getPlayer()));
    }
}
