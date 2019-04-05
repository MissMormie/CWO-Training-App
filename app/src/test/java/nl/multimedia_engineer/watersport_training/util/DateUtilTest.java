package nl.multimedia_engineer.watersport_training.util;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class DateUtilTest {




    @Test
    public void nextOccurence_SameDayPastTime_Test() {
        // This test will fail when run at 0:00;
        Calendar calendar = Calendar.getInstance();
        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        Calendar expected = Calendar.getInstance();
        expected.add(Calendar.DAY_OF_MONTH, 7);
        expected.set(Calendar.HOUR_OF_DAY, 0);
        expected.set(Calendar.MINUTE, 0);

        Calendar result = DateUtil.nextOccurence(dow, 0, 0);

        // Setting both result and expected milli seconds to 0 because they're likely not the same but are irrelevant.
        clearSecondsAndMilliSeconds(result, expected);

        assertTrue("expecting: " + expected.getTime() + " got: " + result.getTime(), result.equals(expected));

    }

    @Test
    public void nextOccurence_SameDayBeforeTime_Test() {
        // This test will fail when run at 23:59;
        Calendar calendar = Calendar.getInstance();
        int dow = calendar.get(Calendar.DAY_OF_WEEK);

        Calendar expected = Calendar.getInstance();
        expected.add(Calendar.DAY_OF_MONTH, 0); // stays same day
        expected.set(Calendar.HOUR_OF_DAY, 23);
        expected.set(Calendar.MINUTE, 59);

        Calendar result = DateUtil.nextOccurence(dow, 23, 59);

        // Setting both result and expected milli seconds to 0 because they're likely not the same but are irrelevant.
        clearSecondsAndMilliSeconds(result, expected);

        assertTrue("expecting: " + expected.getTime() + " got: " + result.getTime(), result.equals(expected));

    }

    @Test
    public void nextOccurence_tomorrow_Test() {
        // This test will fail when run at 23:59;
        Calendar calendar = Calendar.getInstance();
        int dow = calendar.get(Calendar.DAY_OF_WEEK) +1;
        int hour = 10;
        int min = 10;

        Calendar expected = Calendar.getInstance();
        expected.add(Calendar.DAY_OF_MONTH, 1); // tomorrow
        expected.set(Calendar.HOUR_OF_DAY, hour);
        expected.set(Calendar.MINUTE, min);

        Calendar result = DateUtil.nextOccurence(dow, hour, min);

        // Setting both result and expected milli seconds to 0 because they're likely not the same but are irrelevant.
        clearSecondsAndMilliSeconds(result, expected);


        assertTrue("expecting: " + expected.getTime() + " got: " + result.getTime(), result.equals(expected));

    }

    private void clearSecondsAndMilliSeconds(Calendar... calendars) {
        for(Calendar cal : calendars) {
            cal.set(Calendar.MILLISECOND, 0);
            cal.set(Calendar.SECOND, 0);
        }
    }

}