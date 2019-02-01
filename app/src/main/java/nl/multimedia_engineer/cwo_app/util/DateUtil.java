package nl.multimedia_engineer.cwo_app.util;

import java.util.Calendar;

public class DateUtil {
    public enum WeekDays {
        MONDAY(1), TUESDAY(2), WEDNESDAY(3), THURSDAY(4), FRIDAY(5), SATURDAY(6), SUNDAY(7);

        int weekday;
        WeekDays(int weekday) {
            this.weekday = weekday;
        }

        public int getInt() {
            return weekday;
        }
    }

    /**
     * day of week according to java spec, Monday = 1, Sunday = 7.
     * @param dayOfWeek
     * @param hour
     * @param min
     * @return
     */
    public static Calendar nextOccurence(int dayOfWeek, int hour, int min) {

        Calendar calendar = Calendar.getInstance();// This test will fail when run at 0:00;
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // If we're on the required day of week, but passed the time.
        if  ( dayOfWeek == calendar.get(Calendar.DAY_OF_WEEK) &&
            ( currentHour > hour || ( currentHour == hour && currentMinute > min ))) {
            // passed notification time.
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }


        int diff = dayOfWeek - calendar.get(Calendar.DAY_OF_WEEK);
        if(diff < 0) {
            diff += 7;
        }

        calendar.add(Calendar.DAY_OF_MONTH, diff);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);

        return calendar;
    }

}
