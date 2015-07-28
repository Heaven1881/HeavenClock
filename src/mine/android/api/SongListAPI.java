package mine.android.api;

import mine.android.api.database.DataBase;
import mine.android.api.modules.Song;

import java.util.Date;
import java.util.List;

/**
 * Created by Heaven on 15/7/21
 */
public class SongListAPI {
    private static List<Song> list = null;

    private static void save() {
        DataBase<Song> db = new DataBase<Song>(Song.class);
        db.replaceAll(list);
    }

    public static List<Song> get() {
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

    /**
     * 添加歌曲
     * @param song song
     */
    public static void addSong(Song song) {
        List<Song> songs = get();
        song.setPlayTime(new Date());
        songs.add(song);
        save();
    }

    /**
     * 清理多余歌曲
     */
    public static void deleteExtraSong() {
        int historySong = ConfigAPI.get().getHistorySong();
        List<Song> songs = get();
        while (songs.size() > historySong) {
            songs.remove(0);
        }
    }
}
