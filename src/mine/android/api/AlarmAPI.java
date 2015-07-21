package mine.android.api;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import mine.android.api.modules.ClockEntry;
import mine.android.view.AlarmView;

import java.util.Calendar;

/**
 * Created by Heaven on 15/7/21
 */
public class AlarmAPI {
    /**
     * 获取闹钟下一次的激活时间
     *
     * @param clockEntry entry
     * @return calendar
     */
    private static Calendar getNextAlarmTime(ClockEntry clockEntry) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, clockEntry.getHourOfDay());
        c.set(Calendar.MINUTE, clockEntry.getMinute());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        if (!clockEntry.isActive())
            return null;

        switch (clockEntry.getType()) {
            case FOR_ONCE:
            case FOR_DAY:
                if (c.getTimeInMillis() < System.currentTimeMillis()) {
                    c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 1);
                    Log.i("alarmAPI", "add one day for once and day");
                }
                break;
            case FOR_WEEK:
                int startDay = c.get(Calendar.DAY_OF_WEEK);
                if (c.getTimeInMillis() < System.currentTimeMillis()) {
                    startDay = (startDay + 1) % 7;
                }
                for (int i = startDay; i < 7; i++) {
                    if (clockEntry.weeks(i % 7)) {
                        c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + (c.get(Calendar.DAY_OF_WEEK) - startDay));
                        break;
                    }
                }
                break;
            default:
                assert (false);
        }
        return c;
    }

    /**
     * 为闹钟设置定时器
     *
     * @param id       闹钟id
     * @param calendar 时间
     */
    private static void setTimer(int id, Calendar calendar) {
        Intent intent = new Intent(ContextAPI.get(), AlarmView.class);
        intent.putExtra("id", id);

        PendingIntent pendingIntent = PendingIntent.getActivity(ContextAPI.get(), id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) ContextAPI.get().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    /**
     * 取消定时器
     *
     * @param id id
     */
    private static void cancelTimer(int id) {
        Intent intent = new Intent(ContextAPI.get(), AlarmView.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ContextAPI.get(), id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) ContextAPI.get().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * 激活闹钟
     *
     * @param id id
     */
    public static void activeClock(int id) {
        ClockEntry clockEntry = ClockEntryAPI.getById(id);
        if (!clockEntry.isActive()) {
            clockEntry.setActive(true);
            ClockEntryAPI.updateClockEntry(clockEntry);
        }
        Calendar nextAlarmTime = getNextAlarmTime(clockEntry);
        Log.i("next alarm time for id: " + id, nextAlarmTime.getTime().toString());
        setTimer(id, nextAlarmTime);
    }

    /**
     * 取消闹钟
     *
     * @param id id
     */
    public static void cancelClock(int id) {
        Log.i("cancel clock", "" + id);
        ClockEntry clockEntry = ClockEntryAPI.getById(id);
        if (clockEntry != null && clockEntry.isActive()) { //这里clockEntry有可能已经删除了
            clockEntry.setActive(false);
            ClockEntryAPI.updateClockEntry(clockEntry);
        }
        cancelTimer(id);
    }
}
