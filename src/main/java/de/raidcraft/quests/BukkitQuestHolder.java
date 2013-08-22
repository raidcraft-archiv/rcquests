package de.raidcraft.quests;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.quests.api.AbstractQuestHolder;
import de.raidcraft.quests.api.Quest;
import de.raidcraft.quests.api.QuestTemplate;
import de.raidcraft.quests.tables.TPlayer;
import de.raidcraft.quests.tables.TPlayerQuest;

/**
 * @author Silthus
 */
public class BukkitQuestHolder extends AbstractQuestHolder {

    public BukkitQuestHolder(int id, String player) {

        super(id, player);
    }

    @Override
    public void startQuest(QuestTemplate template) {

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

        SimpleQuest quest = new SimpleQuest(table.getId(), template, this);
        addQuest(quest);
        quest.start();
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(QuestPlugin.class);
        TPlayer player = database.find(TPlayer.class, getId());
        player.setActiveQuests(getActiveQuests().size());
        player.setCompletedQuests(getCompletedQuests().size());
        database.save(player);
        // also save all quests the player has
        for (Quest quest : getAllQuests()) {
            quest.save();
        }
    }
}
