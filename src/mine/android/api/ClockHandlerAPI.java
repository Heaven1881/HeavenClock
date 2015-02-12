package mine.android.api;

import mine.android.HeavenClock.MainActivity;
import mine.android.db.DataBase;
import mine.android.modules.ClockItem;

/**
 * Created by Heaven on 2015/2/12.
 */
public class ClockHandlerAPI {

    public static void addClockItem(ClockItem item) {
        DataBase<ClockItem> clockItemDataBase = new DataBase<ClockItem>(ClockItem.class, MainActivity.getContext());
    }
}
