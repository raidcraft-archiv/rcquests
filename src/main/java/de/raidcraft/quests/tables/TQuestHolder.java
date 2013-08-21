package de.raidcraft.quests.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "quests_quest_holder")
public class TQuestHolder {

    @Id
    private int id;
    @NotNull
    @Column(unique = true)
    private String player;
    private int activeQuests;
    private int completedQuests;
}
