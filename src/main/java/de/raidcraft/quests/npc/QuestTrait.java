package de.raidcraft.quests.npc;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Philip Urban
 */
public class QuestTrait extends Trait {
    private static Map<String, NPC> questNpcs = new HashMap<>();

    @Persist
    private String hostID;

    public QuestTrait() {
        super("quest");
    }

    public String getHostId() {
        return hostID;
    }

    public void setHostId(String hostId) {
        this.hostID = hostId;
    }

    public static NPC getNPC(String hostID) {
        return questNpcs.get(hostID);
    }

    @Override
    public void onSpawn() {
        questNpcs.put(hostID, getNPC());
    }

    @Override
    public void onDespawn() {
        questNpcs.remove(hostID);
    }
}
