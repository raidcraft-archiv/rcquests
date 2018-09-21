package de.raidcraft.quests.tables;

import io.ebean.annotation.NotNull;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
public class TPlayerObjective {

    @Id
    private int id;
    @NotNull
    @ManyToOne
    private TPlayerQuest quest;
    @NotNull
    private int objectiveId;
    private Timestamp completionTime;
    private Timestamp abortionTime;
}
