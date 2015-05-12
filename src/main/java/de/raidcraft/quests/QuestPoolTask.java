package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.quests.api.events.QuestPoolQuestStartedEvent;
import de.raidcraft.quests.api.holder.QuestHolder;
import de.raidcraft.quests.api.quest.Quest;
import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.tables.TPlayerQuest;
import de.raidcraft.quests.tables.TPlayerQuestPool;
import net.md_5.bungee.api.ChatColor;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * @author mdoering
 */
public class QuestPoolTask implements Runnable {

    private final QuestPool questPool;
    private final QuestHolder questHolder;

    public QuestPoolTask(QuestPool questPool, QuestHolder questHolder) {

        this.questPool = questPool;
        this.questHolder = questHolder;
    }

    @Override
    public void run() {

        Optional<TPlayerQuestPool> entry = questPool.getDatabaseEntry(questHolder);
        if (!entry.isPresent()) {
            return;
        }
        TPlayerQuestPool dbPool = entry.get();
        // first lets check if the cooldown has passed since the last reset
        if (Instant.now().isBefore(dbPool.getLastReset().toInstant().plusSeconds((long) questPool.getCooldown()))) {
            return;
        }
        // check if the time now is before the defined reset time and abort
        if (questPool.getResetTime().isPresent() && LocalTime.now().isBefore(questPool.getResetTime().get())) {
            return;
        }
        // lets see if we need to abort all active quest pool quests
        if (questPool.isAbortPrevious()) {
            List<TPlayerQuest> activeQuests = questPool.getActiveQuests(dbPool);
            for (TPlayerQuest activeQuest : activeQuests) {
                Optional<Quest> optional = questHolder.getQuest(activeQuest.getQuest());
                if (optional.isPresent() && optional.get().isActive()) {
                    optional.get().abort();
                }
            }
        }
        // ok we are good to go lets see if we can get any new quests
        List<QuestTemplate> result = questPool.getResult(questHolder);
        if (result.isEmpty()) {
            return;
        }
        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        // ok we got some quests, lets reset the timer
        int startCount = 0;
        dbPool.setLastReset(Timestamp.from(Instant.now()));
        for (QuestTemplate questTemplate : result) {
            try {
                Quest quest = questHolder.startQuest(questTemplate);
                TPlayerQuest playerQuest = database.find(TPlayerQuest.class, quest.getId());
                if (playerQuest != null) {
                    playerQuest.setQuestPool(dbPool);
                    database.update(playerQuest);
                    RaidCraft.callEvent(new QuestPoolQuestStartedEvent(quest, dbPool));
                    startCount++;
                }
            } catch (QuestException e) {
                questHolder.sendMessage(ChatColor.RED + "Unable to start QuestPool Quest: " + e.getMessage());
            }
        }
        if (startCount > 0) {
            dbPool.setLastStart(Timestamp.from(Instant.now()));
        }
        database.update(dbPool);
    }
}
