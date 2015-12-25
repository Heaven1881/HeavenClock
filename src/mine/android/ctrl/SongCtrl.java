package mine.android.ctrl;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import mine.android.api.ContextAPI;
import mine.android.api.DouBanPlayer;
import mine.android.api.DoubanAPI;
import mine.android.api.SongListAPI;
import mine.android.api.modules.ClockEntry;
import mine.android.api.modules.Song;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Heaven on 15/7/21
 */
public class SongCtrl implements DouBanPlayer.OnNewSongListener {
    private WebView webView = null;
    private Handler handler = null;
    private DouBanPlayer player = null;
    private ClockEntry entry = null;

    public SongCtrl(Handler handler, WebView webView, DouBanPlayer player) {
        this.webView = webView;
        this.handler = handler;
        this.player = player;
        this.player.setOnNewSongListener(this);
    }

    public void unlikeCurrentSong() {
        player.markCurrentSong(DoubanAPI.OP_BYE);
        player.skipCurrentSong();
        ContextAPI.makeToast("歌曲标记为不再播放");
    }

    public void markCurrentSong() {
        player.markCurrentSong(DoubanAPI.OP_MARK_AS_LIKE);
        ContextAPI.makeToast("已标记为喜欢");
    }

    public void unmarkCurrentSong() {
        player.markCurrentSong(DoubanAPI.OP_DMARK_LIKE);
        ContextAPI.makeToast("已取消标记");
    }

    public void skipCurrentSong() {
        player.markCurrentSong(DoubanAPI.OP_SKIP);
        player.skipCurrentSong();
    }

    public String getCurrentSongAndClock() {
        Song song = player.getCurrentSong();
        JSONObject json = new JSONObject();

        try {
            json.put("time", ClockCtrl.formatTime(entry.getHourOfDay(), entry.getMinute()));
            if (song == null) {      // 若当前没有正在播放的歌曲
                json.put("music", "Loading...");
                json.put("artist", "Loading...");
                json.put("like", false);
                json.put("sid", 0);
            } else {
                json.put("music", song.getTitle());
                json.put("artist", song.getArtist());
                json.put("like", song.isLike());
                json.put("sid", song.getSid());
            }

            String jsonStr = json.toString();
            Log.i("getCurrentSongAndClock", jsonStr);
            return jsonStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    @Override
    public void onNewSong(final Song song) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:doUpdate()");

                Log.i("mp3 url   :", song.getUrl());
                Log.i("mp3 title :", song.getTitle());
                Log.i("mp3 artist:", song.getArtist());
                Log.i("mp3 sid   :", String.valueOf(song.getSid()));
                Log.i("mp3 like  :", String.valueOf(song.isLike()));
                SongListAPI.addSong(song);
            }
        });
    }

    public void setEntry(ClockEntry entry) {
        this.entry = entry;
    }


}
