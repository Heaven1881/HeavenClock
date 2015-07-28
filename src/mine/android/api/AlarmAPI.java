package mine.android.api;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import mine.android.api.modules.ClockEntry;
import mine.android.view.AlarmView;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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
        Calendar nextAlarmTime = getNextAlarmTime(clockEntry);
        Log.i("next alarm time for id: " + id, nextAlarmTime.getTime().toString());
        setTimer(id, nextAlarmTime);

        // make toast
        Calendar current = Calendar.getInstance();
        int divHour = nextAlarmTime.get(Calendar.HOUR_OF_DAY) - current.get(Calendar.HOUR_OF_DAY);
        current.setTimeInMillis(nextAlarmTime.getTimeInMillis() - current.getTimeInMillis());

        // TRAP: 这里使用非常粗鲁的方式强行将时间本地化
        current.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY) - 8);

        String line = "闹钟将在" + (current.get(Calendar.DAY_OF_YEAR) - 1) + "天" + current.get(Calendar.HOUR_OF_DAY) + "小时" + current.get(Calendar.MINUTE) + "分之后响";
        ContextAPI.makeToast(line);
    }

    /**
     * 取消闹钟
     *
     * @param id id
     */
    public static void cancelClock(int id) {
        Log.i("cancel clock", "" + id);
        ClockEntry clockEntry = ClockEntryAPI.getById(id);
        cancelTimer(id);
    }

    /**
     * 检查并激活所有闹钟
     */
    public static void activeAllClock() {
        List<ClockEntry> clockEntries = ClockEntryAPI.get();
        for (ClockEntry entry : clockEntries) {
            if (entry.isActive()) {
                activeClock(entry.getId());
            }
        }
    }
}
