package de.raidcraft.quests.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "quests_player_requirement_count")
public class TPlayerRequirementCount {

    @Id
    private int id;
    @NotNull
    private int requirementId;
    @ManyToOne
    @NotNull
    private TPlayerObjective objective;
    private int count;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public int getRequirementId() {

        return requirementId;
    }

    public void setRequirementId(int requirementId) {

        this.requirementId = requirementId;
    }

    public TPlayerObjective getObjective() {

        return objective;
    }

    public void setObjective(TPlayerObjective objective) {

        this.objective = objective;
    }

    public int getCount() {

        return count;
    }

    public void setCount(int count) {

        this.count = count;
    }
}
