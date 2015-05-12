package de.raidcraft.quests.tables;

import com.avaje.ebean.validation.NotNull;
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
import java.util.ArrayList;
import java.util.List;

/**
 * @author mdoering
 */
@Entity
@Table(name = "quests_player_pools")
@Getter
@Setter
public class TPlayerQuestPool {

    @Id
    private int id;
    @NotNull
    @ManyToOne
    private TPlayer player;
    private String questPool;
    private Timestamp lastStart;
    private Timestamp lastCompletion;
    private Timestamp lastReset;
    private int successiveQuestCounter = 0;
    @JoinColumn(name = "quest_pool_id")
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<TPlayerQuest> quests = new ArrayList<>();
}
