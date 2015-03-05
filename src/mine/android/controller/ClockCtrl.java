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
import mine.android.modules.adapter.ClockItemListAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
            if (ci.isActivated())
                activateClockItem(ci);
        }
    }

    public static void addClockItem(Date date) {
        ClockItem clockItem = new ClockItem(date, ClockItem.NO_REPEAT);
        ClockAPI.addClockItemAPI(clockItem);
        activateClockItem(clockItem);
    }

    public static void addClockItem(ClockItem item) {
        ClockAPI.addClockItemAPI(item);
        activateClockItem(item);
        Log.i("add clock", item.getCompareId() + ":" + item.getDescription() + ":" + item.getRepeat());
    }

    private static void activateClockItem(ClockItem clock) {
        Calendar c = Calendar.getInstance();
        Date time = clock.getTime();
        c.set(Calendar.HOUR_OF_DAY, time.getHours());
        c.set(Calendar.MINUTE, time.getMinutes());
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        if (c.getTimeInMillis() < System.currentTimeMillis()) {
            Calendar tmp = Calendar.getInstance();
            c.set(Calendar.DAY_OF_YEAR, tmp.get(Calendar.DAY_OF_YEAR) + 1);
            Log.d("set clock " + clock.getCompareId(), "set to next day");
        }

        // 添加传参 区分一次性闹钟
        Intent intent = new Intent(MainActivity.getContext(), AlarmActivity.class);
        if (clock.getRepeat() == ClockItem.NO_REPEAT)
            intent.putExtra("once", true);
        else
            intent.putExtra("once", false);
        intent.putExtra("compareId", clock.getCompareId());

        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.getContext(), clock.getCompareId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) MainActivity.getContext().getSystemService(Context.ALARM_SERVICE);

        if (clock.getRepeat() == ClockItem.NO_REPEAT) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
//            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3 * 1000, pendingIntent);
        } else if (clock.getRepeat() == ClockItem.EVERY_DAY) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else if (clock.getRepeat() == ClockItem.EVERY_WEEK) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }

        MainActivity.toastClockItem(clock);
        Log.d("Activate CLock - time", time.getHours() + ":" + time.getMinutes() + " " + clock.getRepeat() + " " + clock.getCompareId());
        Log.d("act", clock.getCompareId() + "");
    }

    private static void dActivateClockItem(ClockItem clock) {
        Calendar c = Calendar.getInstance();
        c.setTime(clock.getTime());

        Intent intent = new Intent(MainActivity.getContext(), AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.getContext(), clock.getCompareId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) MainActivity.getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        Log.d("dact", clock.getCompareId() + "");
    }

    public static void setClockItemDisableByCompareId(int compareId) {
        Intent intent = new Intent(MainActivity.getContext(), AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.getContext(), compareId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) MainActivity.getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

        ClockAPI.updateClockItem(compareId, ClockItem.FIELD_ACTIVATED, false);

        Log.d("dAct by id", String.valueOf(compareId));
    }

    public static void delClockItem(int index) {
        ClockAPI.delClockItemAPI(index);
    }

    public static void setClockItemEnable(ClockItem item, boolean enable) {
        if (item.isActivated() == enable)
            return;

        ClockAPI.updateClockItem(item, ClockItem.FIELD_ACTIVATED, enable);
        if (enable)
            activateClockItem(item);
        else {
            dActivateClockItem(item);
        }
        Log.d("Set Enable", item.getCompareId() + ":" + enable);
    }

    public static ClockItem getClockItemByPos(int position) {
        List<ClockItem> clockItems = ClockAPI.getClockListAPI();
        return clockItems.get(position);
    }

    public static void replaceClockItem(int position, ClockItem item) {
        ClockItem clock = getClockItemByPos(position);
        dActivateClockItem(clock);
        ClockAPI.delClockItemAPI(position);
        ClockCtrl.addClockItem(item);
    }
}
