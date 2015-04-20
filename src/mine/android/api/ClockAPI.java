package mine.android.api;

import android.util.Log;
import mine.android.HeavenClock.MainActivity;
import mine.android.db.DataBase;
import mine.android.modules.ClockItem;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Heaven on 2015/2/12
 */
public class ClockAPI {

    private static DataBase<ClockItem> db = null;

    private static synchronized DataBase<ClockItem> getDb() {
        if (db == null) {
            db = new DataBase<ClockItem>(ClockItem.class, MainActivity.getContext());
        }
        return db;
    }

    public static void addClockItemAPI(ClockItem item) {
        getDb().addItem(Collections.singletonList(item));
    }

    public static void delClockItemAPI(int index) {
        List<ClockItem> clockItems = getDb().readAll();
        clockItems.remove(index);
        getDb().replaceAll(clockItems);
    }

    public static List<ClockItem> getClockListAPI() {
        return getDb().readAll();
    }

    public static void updateClockItem(int compareId, int field, Object value) {
        List<ClockItem> list = getDb().readAll();

        for (ClockItem ci : list) {
            if (ci.getCompareId() == compareId) {
                updateClockItem(ci, field, value);
            }
        }
    }

    public static void updateClockItem(ClockItem item, int field, Object value) {
        List<ClockItem> clockItems = getDb().readAll();

        int i = 0;
        for (ClockItem ci : clockItems) {
            if (ci.compareTo(item) == 0) {
                switch (field) {
                    case ClockItem.FIELD_ACTIVATED:
                        ci.setActivated((Boolean) value);
                        break;
                    case ClockItem.FIELD_DESCRIPTION:
                        ci.setDescription((String) value);
                        break;
                    case ClockItem.FIELD_REPEAT:
                        ci.setRepeat((Integer) value);
                        break;
                    case ClockItem.FIELD_TIME:
                        ci.setTime((Date) value);
                        break;
                    default:
                        assert false;
                }
                Log.d("clock " + ci.getCompareId(), "set " + field + " to " + value);
                clockItems.set(i, ci);
            }
            i = i + 1;
        }

        getDb().replaceAll(clockItems);
    }
}
