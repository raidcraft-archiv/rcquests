package de.raidcraft.quests.tables;

import com.avaje.ebean.validation.NotNull;
import de.raidcraft.quests.api.quest.Quest;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Getter
@Setter
public class TPlayerQuest {

    @Id
    private int id;
    @NotNull
    @ManyToOne
    private TPlayer player;
    private String quest;
    private Quest.Phase phase;
    private Timestamp startTime;
    private Timestamp completionTime;
    @ManyToOne
    private TPlayerQuestPool questPool;
    @JoinColumn(name = "quest_id")
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<TPlayerObjective> objectives;
}
