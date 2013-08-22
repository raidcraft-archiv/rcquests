package de.raidcraft.quests.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author Silthus
 */
@Entity
@Table(name = "quests_player_objectives")
public class TPlayerObjective {

    @Id
    private int id;
    @NotNull
    @ManyToOne
    private TPlayerQuest quest;
    @NotNull
    private int objectiveId;
    private Timestamp completionTime;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TPlayerQuest getQuest() {

        return quest;
    }

    public void setQuest(TPlayerQuest quest) {

        this.quest = quest;
    }

    public int getObjectiveId() {

        return objectiveId;
    }

    public void setObjectiveId(int objectiveId) {

        this.objectiveId = objectiveId;
    }

    public Timestamp getCompletionTime() {

        return completionTime;
    }

    public void setCompletionTime(Timestamp completionTime) {

        this.completionTime = completionTime;
    }
}
