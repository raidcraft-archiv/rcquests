package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.holder.AbstractQuestHolder;
import de.raidcraft.api.quests.quest.Quest;
import de.raidcraft.api.quests.quest.QuestTemplate;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.quests.tables.TPlayerQuest;

import java.util.List;

/**
 * @author Silthus
 */
public class BukkitQuestHolder extends AbstractQuestHolder {

    public BukkitQuestHolder(int id, String player) {

        super(id, player);
        loadExistingQuests();
    }

    private void loadExistingQuests() {

        QuestManager component = RaidCraft.getComponent(QuestManager.class);
        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        List<TPlayerQuest> quests = database.find(TPlayerQuest.class).where().eq("player_id", getId()).findList();
        for (TPlayerQuest quest : quests) {
            try {
                QuestTemplate questTemplate = component.getQuestTemplate(quest.getQuest());
                SimpleQuest simpleQuest = new SimpleQuest(quest, questTemplate, this);
                addQuest(simpleQuest);
            } catch (QuestException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
    }

    @Override
    public Quest createQuest(QuestTemplate template) {

        if (hasQuest(template)) {
            return getQuest(template);
        }
        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayerQuest table = database.find(TPlayerQuest.class).where()
                .eq("player_id", getId())
                .eq("quest", template.getId()).findUnique();
        if (table == null) {
            table = new TPlayerQuest();
            table.setPlayer(database.find(TPlayer.class, getId()));
            table.setQuest(template.getId());
            database.save(table);
        }
        SimpleQuest quest = new SimpleQuest(table, template, this);
        addQuest(quest);
        return quest;
    }

    @Override
    public Quest startQuest(QuestTemplate template) throws QuestException {

        super.startQuest(template);
        Quest quest = createQuest(template);
        if (quest.isCompleted()) {
            throw new QuestException("Du hast diese Quest bereits abgeschlossen.");
        }
        if (!quest.isActive()) {
            quest.start();
        }
        return quest;
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayer player = database.find(TPlayer.class, getId());
        if (player == null) return;
        player.setActiveQuests(getActiveQuests().size());
        player.setCompletedQuests(getCompletedQuests().size());
        database.save(player);
        // also save all quests the player has
        for (Quest quest : getAllQuests()) {
            quest.save();
        }
    }
}
