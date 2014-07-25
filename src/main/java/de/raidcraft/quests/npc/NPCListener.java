package de.raidcraft.quests.npc;

import de.raidcraft.api.quests.QuestHost;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Author: Philip
 * Date: 16.03.13 - 02:19
 */
public class NPCListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onNpcInteract(NPCRightClickEvent event) {

        if (event.getNPC().hasTrait(QuestTrait.class)) {
            QuestHost questHost = event.getNPC().getTrait(QuestTrait.class).getQuestHost();
            if (questHost != null) {
                questHost.interact(event.getClicker());
            }
        }
    }
}