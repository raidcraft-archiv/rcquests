package de.raidcraft.quests.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "quests_players")
public class TPlayer {

    @Id
    private int id;
    @NotNull
    @Column(unique = true)
    private String player;
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<TPlayerQuest> quests;
    private int activeQuests;
    private int completedQuests;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getPlayer() {

        return player;
    }

    public void setPlayer(String player) {

        this.player = player;
    }

    public int getActiveQuests() {

        return activeQuests;
    }

    public void setActiveQuests(int activeQuests) {

        this.activeQuests = activeQuests;
    }

    public int getCompletedQuests() {

        return completedQuests;
    }

    public void setCompletedQuests(int completedQuests) {

        this.completedQuests = completedQuests;
    }

    public List<TPlayerQuest> getQuests() {

        return quests;
    }

    public void setQuests(List<TPlayerQuest> quests) {

        this.quests = quests;
    }
}
