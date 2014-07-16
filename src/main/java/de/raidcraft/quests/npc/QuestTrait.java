package de.raidcraft.quests.npc;

import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;

/**
 * @author Philip Urban
 */
public class QuestTrait extends Trait {

    @Persist
    private String hostId;

    public QuestTrait() {
        super("quest");
    }

    public String getHostId() {

        return hostId;
    }

    public void setHostId(String hostId) {

        this.hostId = hostId;
    }
}
