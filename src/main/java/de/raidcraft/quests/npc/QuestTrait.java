package de.raidcraft.quests.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.api.quests.InvalidQuestHostException;
import de.raidcraft.api.quests.QuestHost;
import de.raidcraft.api.quests.Quests;
import de.raidcraft.quests.QuestPlugin;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;

import javax.annotation.Nullable;
import java.util.HashMap;
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

    public NPC getNpc() {

        return getNPC(getHostId());
    }

    @Nullable
    public QuestHost getQuestHost() {

        try {
            return Quests.getQuestHost(getHostId());
        } catch (InvalidQuestHostException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
            NPC_Manager.getInstance().removeNPC(getNPC(), RaidCraft.getComponent(QuestPlugin.class).getName());
        }
        return null;
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
