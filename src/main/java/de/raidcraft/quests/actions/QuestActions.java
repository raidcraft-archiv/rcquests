package de.raidcraft.quests.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.quests.QuestException;
import de.raidcraft.api.quests.QuestType;
import de.raidcraft.quests.QuestManager;
import de.raidcraft.quests.api.QuestTemplate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
@QuestType.Name("quest")
public class QuestActions implements QuestType {

    @Method(name = "start", type = Type.ACTION)
    public static void start(Player player, ConfigurationSection data) throws QuestException {

        QuestManager component = RaidCraft.getComponent(QuestManager.class);
        QuestTemplate quest = component.getQuestTemplate(data.getString("quest"));
        if (quest == null) {
            throw new QuestException("Wrong config! Unknown quest given: " + data.getString("quest"));
        }
        component.getPlayer(player).startQuest(quest);
    }
}
