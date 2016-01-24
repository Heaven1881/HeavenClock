package mine.android.api;

import mine.android.api.database.DataManager;
import mine.android.api.modules.Json;
import mine.android.api.modules.JsonArray;

import java.util.TreeMap;

/**
 * Created by Heaven on 15/7/19
 */
public class ClockAPI {
    public static String WEEKDAY[] = {
            "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    };

    /* json example
        {
            "cid": 1,
            "type": "FOR_ONCE" | "FOR_DAY" | "FOR_WEEK",
            "time": "09:30",
            "active": true,
            "week_option": "3,4,6"
            "desc": "起床"
        }
     */
    private static TreeMap<Integer, Json> dataSet = null;
    private static int nextCid = 1;

    private static TreeMap<Integer, Json> loadData() {
        if (dataSet != null)
            return dataSet;
        synchronized ("ClockEntry") {
            if (dataSet != null)
                return dataSet;
            DataManager dataManager = new DataManager("ClockEntry");
            dataSet = dataManager.getMap();
            for (Integer cid : dataSet.keySet()) {
                nextCid = max(nextCid, cid + 1);
            }
            return dataSet;
        }
    }

    private static int max(int a, int b) {
        return a > b ? a : b;
    }

    private static void saveData() {
        DataManager dataManager = new DataManager("ClockEntry");
        dataManager.save(dataSet);
    }

    /**
     * 获取clockentry
     *
     * @param id id
     * @return json
     */
    public static Json getClockEntryById(int id) {
        loadData();
        return dataSet.get(id);
    }

    /**
     * 删除clock entry
     *
     * @param id id
     * @return json
     */
    public static Json deleteClockEntryById(int id) {
        loadData();
        Json ret = dataSet.remove(id);
        saveData();
        return ret;
    }

    /**
     * 写入clock entry, cid为0时代表添加
     *
     * @param json json
     */
    public static int setClockEntry(Json json) {
        loadData();
        int cid = json.getInt("cid");
        if (cid == 0) {
            json.put("cid", nextCid++);
        }
        dataSet.put(json.getInt("cid"), json);
        saveData();
        return json.getInt("cid");
    }

    /**
     * 修改激活状态
     *
     * @param id     id
     * @param active active
     */
    public static void setClockEntryActive(int id, boolean active) {
        loadData();
        Json clock = dataSet.get(id);
        clock.put("active", active);
        saveData();
    }

    /**
     * 获取clock entry列表
     */
    public static JsonArray getClockEntries() {
        loadData();
        return JsonArray.create(dataSet.values());
    }
}
