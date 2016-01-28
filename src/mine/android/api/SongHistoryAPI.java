package mine.android.api;

import android.util.Log;
import mine.android.api.database.DataManager;
import mine.android.api.modules.Json;
import mine.android.api.modules.JsonArray;

import java.util.*;

/**
 * Created by Heaven on 15/7/21
 */
public class SongHistoryAPI {
    /*
    {
        "picture": "http://img4.douban.com/lpic/s2174238.jpg",
        "artist": "薛之谦",
        "url": "http://mr7.doubanio.com/a96e6876431ad1ba832d862e39f959b0/0/fm/song/p1488212_128k.mp4",
        "title": "认真的雪",
        "like": 1,
        "length": 261,
        "sid": "1488212",
        "file_ext": "mp4",
        "played_time": 1466313391,
    }
     */

    private static TreeMap<Integer, Json> dataSet = null;

    private static TreeMap<Integer, Json> loadData() {
        if (dataSet != null)
            return dataSet;
        synchronized ("SongHistory") {
            if (dataSet != null)
                return dataSet;
            DataManager dataManager = new DataManager("SongHistory");
            dataSet = dataManager.getMap();
        }
        return dataSet;
    }

    private static void saveData() {
        DataManager dataManager = new DataManager("SongHistory");
        dataManager.save(dataSet);
    }

    /**
     * 记录新的歌曲
     *
     * @param song json
     */
    public static void recordSongEntry(Json song) {
        loadData();
        int playedTime = (int) (System.currentTimeMillis() / 1000);
        dataSet.put(playedTime, Json.create(
                "picture", song.getString("picture"),
                "artist", song.getString("artist"),
                "url", song.getString("url"),
                "title", song.getString("title"),
                "like", song.getInt("like"),
                "length", song.getInt("length"),
                "sid", song.getInt("sid"),
                "played_time", playedTime
        ));

        // 删除额外的记录
        int historyLimit = ConfigAPI.get().getInt("max_history");
        while (dataSet.size() > historyLimit) {
            Integer key = dataSet.ceilingKey(0);
            dataSet.remove(key);
        }
        saveData();
    }

    /**
     * 获取历史歌曲列表
     *
     * @return json
     */
    public static JsonArray getSongEntries() throws Exception {
        loadData();
        List<Json> songs = new ArrayList<>(dataSet.values());
        Collections.sort(songs, new Comparator<Json>() {
            @Override
            public int compare(Json t1, Json t2) {
                return t2.getInt("played_time") - t1.getInt("played_time");
            }
        });
        return JsonArray.create(songs);
    }

    public static Json getSongEntry(int playeredTime) {
        loadData();
        return dataSet.get(playeredTime);
    }
}
