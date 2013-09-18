package de.raidcraft.quests.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "quests_player_quests")
public class TPlayerQuest {

    @Id
    private int id;
    @NotNull
    @ManyToOne
    private TPlayer player;
    private String quest;
    private Timestamp startTime;
    private Timestamp completionTime;
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<TPlayerObjective> objectives;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TPlayer getPlayer() {

        return player;
    }

    public void setPlayer(TPlayer player) {

        this.player = player;
    }

    public String getQuest() {

        return quest;
    }

    public void setQuest(String quest) {

        this.quest = quest;
    }

    public Timestamp getStartTime() {

        return startTime;
    }

    public void setStartTime(Timestamp startTime) {

        this.startTime = startTime;
    }

    public Timestamp getCompletionTime() {

        return completionTime;
    }

    public void setCompletionTime(Timestamp completionTime) {

        this.completionTime = completionTime;
    }

    public List<TPlayerObjective> getObjectives() {

        return objectives;
    }

    public void setObjectives(List<TPlayerObjective> objectives) {

        this.objectives = objectives;
    }
}
