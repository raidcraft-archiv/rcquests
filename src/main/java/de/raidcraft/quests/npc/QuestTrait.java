package de.raidcraft.quests.npc;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.npc.NPC_Manager;
import de.raidcraft.quests.api.InvalidQuestHostException;
import de.raidcraft.quests.api.QuestHost;
import de.raidcraft.quests.api.Quests;
import de.raidcraft.quests.QuestPlugin;
import lombok.Getter;
import lombok.Setter;
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
    @Getter
    @Setter
    private String hostId;

    public QuestTrait() {
        super("quest");
    }

    public NPC getNpc() {
        return getNPC(getHostId());
    }

    @Nullable
    public QuestHost getQuestHost() {
        try {
            return Quests.getQuestHost(getHostId());
        } catch (InvalidQuestHostException e) {
            RaidCraft.getComponent(QuestPlugin.class).warning(getHostId() + ":" + e.getMessage());
            e.printStackTrace();
            // TODO: why remove NPC?
            RaidCraft.getComponent(QuestPlugin.class).warning("Try to remove NPC: " + getNpc().getName());
            NPC_Manager.getInstance().removeNPC(getNPC(), RaidCraft.getComponent(QuestPlugin.class).getName());
        }
        return null;
    }

    public static NPC getNPC(String hostID) {
        return questNpcs.get(hostID);
    }

    @Override
    public void onAttach() {
        questNpcs.put(getHostId(), getNPC());
    }

    @Override
    public void onDespawn() {
        questNpcs.remove(getHostId());
    }
}
