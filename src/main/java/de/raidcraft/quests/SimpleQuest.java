package de.raidcraft.quests;

import de.raidcraft.quests.api.AbstractQuest;
import de.raidcraft.quests.api.QuestHolder;
import de.raidcraft.quests.api.QuestTemplate;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
public class SimpleQuest extends AbstractQuest {

    public SimpleQuest(QuestTemplate template, QuestHolder holder) {

        super(template, holder);
    }

    @Override
    public void start() {

        setStartTime(new Timestamp(System.currentTimeMillis()));
        // TODO
    }
}
