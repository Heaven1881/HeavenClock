package mine.android.ctrl;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
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
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.markCurrentSong(DoubanAPI.OP_BYE);
                player.skipCurrentSong();
            }
        });
    }

    public void markCurrentSong() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.markCurrentSong(DoubanAPI.OP_MARK_AS_LIKE);
            }
        });
    }

    public void unmarkCurrentSong() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.markCurrentSong(DoubanAPI.OP_DMARK_LIKE);
            }
        });
    }

    public void skipCurrentSong() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.markCurrentSong(DoubanAPI.OP_SKIP);
                player.skipCurrentSong();
            }
        });
    }

    @Override
    public void onNewSong(final Song song) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    json.put("time", ClockCtrl.formatTime(entry.getHourOfDay(), entry.getMinute()));
                    json.put("music", song.getTitle());
                    json.put("artist", song.getArtist());
                    json.put("like", song.isLike());
                    json.put("sid", song.getSid());
                } catch (JSONException ignored) {
                }
                String jsonStr = json.toString();
                webView.loadUrl("javascript:drawAlarm('" + jsonStr + "')");

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
