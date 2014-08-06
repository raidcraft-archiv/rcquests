package de.raidcraft.quests.npc;

import de.raidcraft.rcconversations.npc.NPC_Conservations_Manager;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;

/**
 * @author Dragonfire
 */
public class NPC_Quest_Manager {

    private static NPC_Quest_Manager INSTANCE;


    private NPC_Quest_Manager() {
        // singleton
    }

    public static NPC_Quest_Manager getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new NPC_Quest_Manager();
        }
        return INSTANCE;
    }

    public NPC createNonPersistNpcQuest(String name, String host, String conversationName, String hostID) {

        NPC npc = NPC_Conservations_Manager.getInstance().createNonPersistNpcConservations(name, host, conversationName);
        npc.addTrait(QuestTrait.class);
        npc.getTrait(QuestTrait.class).setHostId(hostID);
        return npc;
    }

    public NPC spawnNonPersistNpcQuest(Location loc, String name, String host, String conversationName, String hostID) {

        NPC npc = this.createNonPersistNpcQuest(name, host, conversationName, hostID);
        npc.spawn(loc);
        return npc;
    }
}
