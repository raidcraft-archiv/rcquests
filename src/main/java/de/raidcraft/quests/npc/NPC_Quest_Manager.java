package de.raidcraft.quests.npc;

import de.raidcraft.api.npc.NPC_Manager;
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
        NPC_Manager.getInstance().store(host);
        return npc;
    }

    public NPC spawnPersistNpcQuest(Location loc, String name, String host, String conversationName, String hostID) {

        NPC npc = this.createNonPersistNpcQuest(name, host, conversationName, hostID);
        npc.spawn(loc);
        NPC_Manager.getInstance().store(host);
        return npc;
    }
}
