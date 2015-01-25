package de.raidcraft.quests;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.holder.QuestHolder;
import de.raidcraft.quests.api.QuestPool;
import de.raidcraft.quests.api.TimeQuestManager;
import de.raidcraft.quests.tables.TTimeQuest;
import org.apache.commons.lang.time.DateUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SimpleTimeQuestManager implements TimeQuestManager {

    public enum CHECK {
        ALLREADY_ONLINE,
        RESET_COUNTER,
        DAY_BONUS
    }

    private QuestPlugin plugin;
    private Map<String, QuestPool> timeQuestPool = new HashMap<>();

    public SimpleTimeQuestManager() {
        plugin = RaidCraft.getComponent(QuestPlugin.class);
        // daily
        QuestPool daily = new SimpleQuestPool(
                new File(plugin.getDataFolder(), TimeQuestManager.TYPE_DAILY),
                TimeQuestManager.TYPE_DAILY);
        timeQuestPool.put(TimeQuestManager.TYPE_DAILY, daily);
    }


    public void reload() {
        plugin = RaidCraft.getComponent(QuestPlugin.class);
        for (QuestPool pool : timeQuestPool.values()) {
            pool.reload();
        }
    }

    @Override
    public boolean checkTimeQuests(String type, QuestHolder holder) {
        QuestPool pool = timeQuestPool.get(type);
        if (pool == null) {
            plugin.warning("No QuestPool found of type " + type + " found for player "
                    + holder.getPlayer().getDisplayName());
            return false;
        }
        TTimeQuest timeQuestStats = plugin.getDatabase().find(TTimeQuest.class)
                .where()
                .eq("type", type)
                .eq("player_id", holder.getId()).findUnique();
        if (timeQuestStats == null) {
            timeQuestStats = new TTimeQuest();
            timeQuestStats.setType(type);
            timeQuestStats.setCounter(0);
            timeQuestStats.setPlayerId(holder.getId());
        } else {
            // TODO: use new JAVA 8 Time API
            Date now = new Date();
            if (DateUtils.isSameDay(now, timeQuestStats.getLastStarted())) {
                // if he was online today -> abort
                return false;
            }
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            if (timeQuestStats.getLastCompleted() == null ||
                    !DateUtils.isSameDay(timeQuestStats.getLastCompleted(), cal.getTime())) {
                // if quest not completed yesterday
                timeQuestStats.setCounter(0);
            }

        }
        return startQuest(pool, holder, timeQuestStats);
    }

    public static CHECK checkPlayerQuest(Date lastStarted, Date lastCompleted) {
        if (DateUtils.isSameDay(new Date(), lastStarted)) {
            return CHECK.ALLREADY_ONLINE;
        }
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        if (lastCompleted == null ||
                !DateUtils.isSameDay(lastCompleted, cal.getTime())) {
            // if quest not completed yesterday
            return CHECK.RESET_COUNTER;
        }
        return CHECK.DAY_BONUS;
    }

    private boolean startQuest(QuestPool pool, QuestHolder holder, TTimeQuest timeQuestStats) {
        timeQuestStats.setLastStarted(new Date());
        timeQuestStats.setLastCompleted(null);
        plugin.getDatabase().save(timeQuestStats);
        return true;
    }
}
