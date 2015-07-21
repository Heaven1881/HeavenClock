package mine.android.api;

import mine.android.api.database.DataBase;
import mine.android.api.modules.Song;

import java.util.List;

/**
 * Created by Heaven on 15/7/21
 */
@Deprecated
public class SongListAPI {
    private static List<Song> list = null;

    private static void save() {
        DataBase<Song> db = new DataBase<Song>(Song.class);
        db.replaceAll(list);
    }

    private static List<Song> get() {
        if (list != null)
            return list;
        synchronized ("ClockEntry") {
            if (list != null)
                return list;
            DataBase<Song> db = new DataBase<Song>(Song.class);
            list = db.readAll();
            return list;
        }
    }

    /**
     * 根据sid获取歌曲
     * @param sid song id
     * @return song
     */
    public static Song getBySongId(int sid) {
        List<Song> songs = get();
        for (Song song : songs) {
            if (song.getSid() == sid)
                return song;
        }
        return null;
    }
}
