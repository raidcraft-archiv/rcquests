package de.raidcraft.quests.tables;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Silthus
 */
@Entity
@Table(name = "quest_timequest")
@Getter
@Setter
public class TTimeQuest {

    @Id
    private int id;
    private int playerId;
    private String type;
    private int counter;
    private Date lastStarted;
    private Date lastCompleted;
}