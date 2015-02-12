package mine.android.api;

import mine.android.HeavenClock.MainActivity;
import mine.android.db.DataBase;
import mine.android.modules.ClockItem;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Heaven on 2015/2/12.
 */
public class ClockAPI {

    public static void addClockItemAPI(ClockItem item) {
        DataBase<ClockItem> db = new DataBase<ClockItem>(ClockItem.class, MainActivity.getContext());
        db.addItem(Arrays.asList(item));
    }

    public static void delClockItemAPI(int index) {
        DataBase<ClockItem> db = new DataBase<ClockItem>(ClockItem.class, MainActivity.getContext());
        List<ClockItem> clockItems = db.readAll();
        clockItems.remove(index);
        db.replaceAll(clockItems);
    }

    public static List<ClockItem> getClockListAPI() {
        DataBase<ClockItem> db = new DataBase<ClockItem>(ClockItem.class, MainActivity.getContext());
        return db.readAll();
    }
}
