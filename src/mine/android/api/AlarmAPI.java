package mine.android.api;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import mine.android.api.modules.Json;
import mine.android.api.modules.JsonArray;
import mine.android.view.CallAlarm;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Heaven on 15/7/21
 */
public class AlarmAPI {
    /**
     * 获取下次激活的事件
     *
     * @param clock clock
     * @return long
     */
    public static long calculateNextAlarm(Json clock) {
        String time = clock.getString("time");
        String type = clock.getString("type");

        String[] t = time.split(":");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(t[0]));
        c.set(Calendar.MINUTE, Integer.parseInt(t[1]));
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        long timestamp = c.getTimeInMillis();

        if (timestamp > System.currentTimeMillis()) // 如果timestemp在当前时间之后,则直接返回
            return timestamp;

        // 否则，根据闹钟种类增加对应的天数
        long ONE_DAY_MILLISECOND = 24 * 3600 * 1000;
        if (type.equals("FOR_ONCE") || type.equals("FOR_DAY")) {
            timestamp += ONE_DAY_MILLISECOND;
        } else if (type.equals("FOR_WEEK")) {
            int curWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
            int minInterval = 7;
            for (String week : clock.getString("week").split(",")) {
                int actWeek = Integer.parseInt(week);
                int interval = (actWeek + 7) - curWeek;
                interval = interval > 7 ? interval - 7 : interval;
                minInterval = minInterval < interval ? minInterval : interval;
            }
            timestamp += minInterval * ONE_DAY_MILLISECOND;
        }
        Log.i("next alarm", String.valueOf(new Date(timestamp)));
        return timestamp;
    }

    /**
     * 为闹钟设置定时器
     *
     * @param cid 闹钟id
     */
    public static void setTimer(int cid, long timestamp) {
        Intent intent = new Intent(ContextAPI.get(), CallAlarm.class);
        intent.putExtra("cid", cid);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(ContextAPI.get(), cid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) ContextAPI.get().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, timestamp, pendingIntent);


    }

    /**
     * 取消定时器
     *
     * @param cid cid
     */
    public static void cancelTimer(int cid) {
        Intent intent = new Intent(ContextAPI.get(), CallAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ContextAPI.get(), cid, intent, 0);
        AlarmManager alarmManager = (AlarmManager) ContextAPI.get().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static void setTimerByClockId(int cid) {
        Json clock = ClockAPI.getClockEntryById(cid);
        if (clock == null) {
            Log.e("setTimer", String.format("clock not find [id=%d]", cid));
            return;
        }
        long timestamp = calculateNextAlarm(clock);
        setTimer(cid, timestamp);

        // make toast
        timestamp -= System.currentTimeMillis();
        String line = String.format("闹钟将在%d小时%d分后响",
                timestamp / (1000 * 3600),
                (timestamp % (1000 * 3600) / (60 * 1000)));
        ContextAPI.makeToast(line);
    }

    public static void cancelTimerByClockId(int cid) {
        cancelTimer(cid);
    }

    public static void setTimerForAllClock() {
        JsonArray clocks = ClockAPI.getClockEntries();
        for (Json clock : clocks) {
            if (clock.getBoolean("active")) {
                long timestamp = calculateNextAlarm(clock);
                setTimer(clock.getInt("cid"), timestamp);
            }
        }
        ContextAPI.makeToast("已为所有闹钟设置定时器");
    }
}
