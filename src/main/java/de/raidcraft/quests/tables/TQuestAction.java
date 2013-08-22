package de.raidcraft.quests.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "quests_executed_actions")
public class TQuestAction {

    @Id
    private int id;
    private int actionId;
    private String name;
    private String provider;
    private String player;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getActionId() {

        return actionId;
    }

    public void setActionId(int actionId) {

        this.actionId = actionId;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getProvider() {

        return provider;
    }

    public void setProvider(String provider) {

        this.provider = provider;
    }

    public String getPlayer() {

        return player;
    }

    public void setPlayer(String player) {

        this.player = player;
    }
}
