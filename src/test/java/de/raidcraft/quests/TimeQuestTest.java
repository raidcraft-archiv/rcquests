package de.raidcraft.quests;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class TimeQuestTest {

    @Test
    public void checkTimeQuests() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.DATE, -2);

        Date now = new Date();

        // if checked today
        assertEquals(SimpleTimeQuestManager.checkPlayerQuest(now, now), SimpleTimeQuestManager.CHECK.ALLREADY_ONLINE);
        assertEquals(SimpleTimeQuestManager.checkPlayerQuest(now, null), SimpleTimeQuestManager.CHECK.ALLREADY_ONLINE);
        // if quest not completed
        assertEquals(SimpleTimeQuestManager.checkPlayerQuest(cal.getTime(), null), SimpleTimeQuestManager.CHECK.RESET_COUNTER);
        // if one day skipped
        assertEquals(SimpleTimeQuestManager.checkPlayerQuest(cal.getTime(), cal.getTime()), SimpleTimeQuestManager.CHECK.DAY_BONUS);
        assertEquals(SimpleTimeQuestManager.checkPlayerQuest(cal2.getTime(), cal2.getTime()), SimpleTimeQuestManager.CHECK.RESET_COUNTER);
    }

}
