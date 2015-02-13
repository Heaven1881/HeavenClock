package mine.android.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.SimpleAdapter;
import mine.android.HeavenClock.AlarmActivity;
import mine.android.HeavenClock.MainActivity;
import mine.android.HeavenClock.R;
import mine.android.api.ClockAPI;
import mine.android.modules.ClockItem;

import java.util.*;

/**
 * Created by Heaven on 2015/2/12.
 */
public class ClockCtrl {
    public static SimpleAdapter getClockListAdapter() {
        List<ClockItem> list = ClockAPI.getClockListAPI();
        ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();

        for (ClockItem ci : list) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            Date ciTime = ci.getTime();
            map.put("ItemTitle", ciTime.getHours() + ":" + ciTime.getMinutes());
            map.put("ItemText", ci.getDescription() != null ? ci.getDescription() : MainActivity.getContext().getResources().getString(R.string.no_description));
            listItem.add(map);
        }

        return new SimpleAdapter(MainActivity.getContext(), listItem, R.layout.list_item, new String[]{"ItemTitle", "ItemText"}, new int[]{R.id.ItemTitle, R.id.ItemText});
    }

    public static void addClockItem(Date date) {
        ClockAPI.addClockItemAPI(new ClockItem(date, ClockItem.NO_REPEAT));

        //TODO add alarmManager
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        Intent intent = new Intent(MainActivity.getContext(), AlarmActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.getContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) MainActivity.getContext().getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 3 * 1000, pendingIntent);

        Log.i("Clock - set    :", "" + c.getTimeInMillis() / 1000);
        Log.i("Clock - current:", "" + System.currentTimeMillis() / 1000);
        Log.i("Clock - devide :", "" + (c.getTimeInMillis() - System.currentTimeMillis()) / 1000);
    }

    public static void delClockItem(int index) {
        ClockAPI.delClockItemAPI(index);
    }
}
