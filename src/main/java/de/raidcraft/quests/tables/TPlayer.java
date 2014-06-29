package de.raidcraft.quests.tables;

import com.avaje.ebean.validation.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "quests_players")
@Getter
@Setter
public class TPlayer {

    @Id
    private int id;
    @NotNull
    @Column(unique = true)
    private String player;
    @JoinColumn(name = "player_id")
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<TPlayerQuest> quests;
    private int activeQuests;
    private int completedQuests;
}
