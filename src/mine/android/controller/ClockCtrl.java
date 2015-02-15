package mine.android.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.BaseAdapter;
import mine.android.HeavenClock.AlarmActivity;
import mine.android.HeavenClock.MainActivity;
import mine.android.api.ClockAPI;
import mine.android.modules.ClockItem;

import java.util.*;

/**
 * Created by Heaven on 2015/2/12.
 */
public class ClockCtrl {
    public static BaseAdapter getClockListForListView() {
        List<ClockItem> list = ClockAPI.getClockListAPI();
        return ClockItemListAdapter.make(MainActivity.getContext(), list);
    }

    public static void activateAllClockItem() {
        List<ClockItem> list = ClockAPI.getClockListAPI();
        for (ClockItem ci : list) {
            activateClockItem(ci);
        }
    }

    public static void addClockItem(Date date) {
        ClockItem clockItem = new ClockItem(date, ClockItem.NO_REPEAT);
        ClockAPI.addClockItemAPI(clockItem);
        activateClockItem(clockItem);
    }

    private static void activateClockItem(ClockItem clock) {
        Calendar c = Calendar.getInstance();
        c.setTime(clock.getTime());

        //TODO 添加传参 区分一次性闹钟
        Intent intent = new Intent(MainActivity.getContext(), AlarmActivity.class);
        intent.putExtra("once", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.getContext(), clock.getCompareId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) MainActivity.getContext().getSystemService(Context.ALARM_SERVICE);
        if (clock.getRepeat() == ClockItem.NO_REPEAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        } else if (clock.getRepeat() == ClockItem.EVERY_DAY) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else if (clock.getRepeat() == ClockItem.EVERY_WEEK) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }

        Log.i("Activate CLock - time", clock.getTime().getHours() + ":" + clock.getTime().getMinutes() + " " + clock.getRepeat() + " " + clock.getCompareId());
    }

    private static void dActivateClockItem(ClockItem clock) {
        Calendar c = Calendar.getInstance();
        c.setTime(clock.getTime());

        Intent intent = new Intent(MainActivity.getContext(), AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.getContext(), clock.getCompareId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) MainActivity.getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public static void delClockItem(int index) {
        ClockAPI.delClockItemAPI(index);
    }

    public static void setClockItemEnable(ClockItem item, boolean enable) {
        ClockAPI.updateClockItem(item, ClockItem.FIELD_ACTIVATED, enable);
        if (enable)
            activateClockItem(item);
        else {
            dActivateClockItem(item);
        }
        Log.i("Set Clock Enable", String.valueOf(enable));
    }
}
