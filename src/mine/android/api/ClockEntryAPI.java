package mine.android.api;

import mine.android.api.database.DataBase;
import mine.android.api.modules.ClockEntry;

import java.util.List;

/**
 * Created by Heaven on 15/7/19
 */
public class ClockEntryAPI {
    private static List<ClockEntry> list = null;
    private static int nextId = 0;

    private static void updateNextId() {
        if (list == null)
            return;
        nextId = 0;
        for (ClockEntry entry : list) {
            if (entry.getId() > nextId) {
                nextId = entry.getId() + 1;
            }
        }
    }

    private static int getNextId() {
        return ++nextId;
    }

    /**
     * 将内存中的值写入磁盘
     *
     * @return 成功为true
     */
    private static boolean save() {
        DataBase<ClockEntry> db = new DataBase<ClockEntry>(ClockEntry.class);
        db.replaceAll(list);
        return true;
    }

    public static List<ClockEntry> get() {
        if (list != null)
            return list;
        synchronized ("ClockEntry") {
            if (list != null)
                return list;
            DataBase<ClockEntry> db = new DataBase<ClockEntry>(ClockEntry.class);
            list = db.readAll();
            updateNextId();
            return list;
        }
    }

    public static ClockEntry getById(int id) {
        List<ClockEntry> clockEntries = get();
        for (ClockEntry entry : clockEntries) {
            if (entry.getId() == id)
                return entry;
        }
        return null;
    }

    /**
     * 查找对应id的clcok entry在列表中的位置
     *
     * @param id id
     * @return 若不存在则返回-1
     */
    private static int findById(int id) {
        List<ClockEntry> clockEntries = get();
        int index = 0;
        for (ClockEntry entry : clockEntries) {
            if (entry.getId() == id)
                return index;
            index++;
        }
        return -1;
    }

    /**
     * 更新clockEntry的信息
     *
     * @param id    对应的id
     * @param entry clock entry
     * @return ture则成功，false则失败
     */
    public static synchronized boolean updateClockEntry(int id, ClockEntry entry) {
        // 检查id 与 entry 是否一致
        if (entry.getId() != id)
            return false;

        // 检查id是否存在
        int entryId = findById(id);
        if (entryId == -1)
            return false;

        // 若存在，则替换对应的entry
        list.remove(entryId);
        list.add(entry);
        //写回
        save();

        return true;
    }

    /**
     * 删除对应id的entry
     *
     * @param id id
     * @return true代表成功
     */
    public static synchronized boolean deleteClockEntry(int id) {
        // 检查id是否存在
        int entryId = findById(id);
        if (entryId == -1)
            return false;

        // 删除对应entry
        list.remove(entryId);
        // 写回
        save();

        return true;
    }

    /**
     * 添加clock entry
     * 函数会自动给传入的entry分配新的id
     *
     * @param entry entry
     * @return true代表成功
     */
    public static synchronized boolean addClockEntry(ClockEntry entry) {
        // 分配新id
        int newId = getNextId();
        entry.setId(newId);

        // 插入
        list.add(entry);
        save();

        return true;
    }
}
