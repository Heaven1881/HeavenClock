package mine.android.ctrl;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import mine.android.api.*;
import mine.android.api.modules.Json;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Heaven on 15/7/21
 */
public class PlayCtrl implements DouBanPlayer.OnNewSongListener {
    private WebView webView = null;
    private Handler handler = null;
    private static ExecutorService threadPool = null;
    private DouBanPlayer player = null;
    private Json clock = null;
    private Json config = null;

    public PlayCtrl(Handler handler, WebView webView) {
        // 初始化线程池
        if (PlayCtrl.threadPool == null)
            PlayCtrl.threadPool = Executors.newSingleThreadExecutor();

        this.webView = webView;
        this.handler = handler;
        this.player = new DouBanPlayer();
        this.player.setOnNewSongListener(this);
        this.config = ConfigAPI.get();
    }

    /**
     * 根据设置,从服务器获取音乐信息，加入player中
     */
    private void loadSong() {
        try {
            Random seed = new Random();
            double pForNew = config.getDouble("p_for_new");
            int repeat = config.getInt("repeat");
            while (PlayCtrl.this.player.sizeOfPlayQueue() < repeat) {
                double v = seed.nextDouble();
                Json response;
                if (v < pForNew)
                    response = DoubanAPI.report(Json.create(
                            "channel", 0,
                            "type", "n"
                    ));
                else
                    response = DoubanAPI.report(Json.create(
                            "channel", -3,
                            "type", "n"
                    ));

                PlayCtrl.this.player.addSong(response.getJsonArray("song"));
            }
        } catch (DoubanAPI.DoubanException e) {
            // TODO ....
            Log.e("Exception", e.getMessage());
        }
    }

    /**
     * 让 player开始工作
     * 函数先从服务器获取足够的音乐,再开始播放
     */
    public void start() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                PlayCtrl.this.loadSong();
                PlayCtrl.this.player.start();
            }
        });
    }

    /**
     * 让player停止工作
     */
    public void stop() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                PlayCtrl.this.player.stopAndRelease();
            }
        });
    }

    public void byeCurrentSong() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Json currentSong = PlayCtrl.this.player.getCurrentSong();
                DoubanAPI.report(Json.create(
                        "channel", 0,
                        "sid", currentSong.getInt("sid"),
                        "type", "b"
                ));
                player.skipCurrentSong();
            }
        });
        ContextAPI.makeToast("歌曲标记为不再播放");
    }

    public void rateCurrentSong() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Json currentSong = PlayCtrl.this.player.getCurrentSong();
                DoubanAPI.report(Json.create(
                        "channel", 0,
                        "sid", currentSong.getInt("sid"),
                        "type", "r"
                ));
            }
        });
        ContextAPI.makeToast("已标记为喜欢");
    }

    public void unrateCurrentSong() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Json currentSong = PlayCtrl.this.player.getCurrentSong();
                DoubanAPI.report(Json.create(
                        "channel", 0,
                        "sid", currentSong.getInt("sid"),
                        "type", "u"
                ));
            }
        });
        ContextAPI.makeToast("已取消标记");
    }

    public void skipCurrentSong() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                Json currentSong = PlayCtrl.this.player.getCurrentSong();
                DoubanAPI.report(Json.create(
                        "channel", 0,
                        "sid", currentSong.getString("sid"),
                        "type", "s"
                ));
                player.skipCurrentSong();
            }
        });

    }

    public String getCurrentSongAndClock() throws Exception {
        Json currentSong = this.player.getCurrentSong();
        if (currentSong == null) {  // 若当前没有正在播放的歌曲
            currentSong = Json.create(
                    "picture", "image/album-bb.jpg",
                    "artist", "Loading...",
                    "title", "Loading...",
                    "like", 0,
                    "sid", 0
            );
        }
        Json json = Json.create(
                "song", currentSong,
                "clock", this.clock
                );
        String jsonStr = json.toString();
        Log.i("getCurrentSongAndClock", jsonStr);
        return jsonStr;
    }

    public void setClock(Json clock) {
        this.clock = clock;
    }


    @Override
    public void onNewSong(Json song) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("PlayCtrl", String.format("title:%s", song.getString("title")));
                Log.i("PlayCtrl", String.format("like:%d", song.getInt("like")));
                SongHistoryAPI.recordSongEntry(song);
                webView.loadUrl("javascript:doUpdate()");
            }
        });
    }
}
