package de.raidcraft.quests.api.player;

import de.raidcraft.quests.api.quest.QuestTemplate;
import de.raidcraft.quests.api.quest.Quest;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Silthus
 */
public interface QuestHolder {

    public int getId();

    public String getName();

    public Player getPlayer();

    public List<Quest> getAllQuests();

    public List<Quest> getCompletedQuests();

    public List<Quest> getActiveQuests();

    public void addQuest(Quest quest);

    public void abortQuest(Quest quest);

    public void startQuest(QuestTemplate template);

    public void save();
}
