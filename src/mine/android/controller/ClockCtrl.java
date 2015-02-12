package mine.android.controller;

import android.widget.SimpleAdapter;
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
            map.put("ItemTitle", ci.getTime().toLocaleString());
            map.put("ItemText", ci.getDescription() != null ? ci.getDescription() : MainActivity.getContext().getResources().getString(R.string.no_description));
            listItem.add(map);
        }

        return new SimpleAdapter(MainActivity.getContext(), listItem, R.layout.list_item, new String[]{"ItemTitle", "ItemText"}, new int[]{R.id.ItemTitle, R.id.ItemText});
    }

    public static void addClockItem(Date date) {
        ClockAPI.addClockItemAPI(new ClockItem(date, ClockItem.NO_REPEAT));

        //TODO add alarmManager
//        Intent intent = new Intent(MainActivity.getContext(), ClockActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.getContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager alarmManager = (AlarmManager) MainActivity.getContext().getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pendingIntent);
    }
}
