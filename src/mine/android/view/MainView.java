package mine.android.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import mine.android.HeavenClock.R;
import mine.android.api.AlarmAPI;
import mine.android.api.ContextAPI;
import mine.android.api.SongHistoryAPI;
import mine.android.api.modules.Json;
import mine.android.ctrl.ClockCtrl;
import mine.android.ctrl.ConfigCtrl;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Heaven on 15/7/18
 */
public class MainView extends Activity {
    private static Context context = null;
    private WebView webView;
    private static ExecutorService threadPool = null;
    private Handler handler = new Handler();
    private SimplePlayer player = null;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;

//        MainView.active = true;

        // 初始化线程池
        if (threadPool == null)
            threadPool = Executors.newCachedThreadPool();

        player = new SimplePlayer();

        webView = (WebView) findViewById(R.id.mainView);
        ContextAPI.initWebView(webView);

        //js java 映射
        webView.addJavascriptInterface(new ClockCtrl(handler, webView), "ClockCtrl");
        webView.addJavascriptInterface(new ConfigCtrl(handler, webView), "ConfigCtrl");
        webView.addJavascriptInterface(this, "Activity");
//        webView.loadUrl("file:///android_asset/clocks.html");
        webView.loadUrl("file:///android_asset/dev/index.html");

        // 激活所有闹钟的定时器
        try {
            AlarmAPI.setTimerForAllClock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String simplePlayByPlayedTime(String jsonStr) {
        Log.i("simplePlaByPlayedTime", jsonStr);
        Json json = Json.parse(jsonStr);
        Json song = SongHistoryAPI.getSongEntry(json.getInt("played_time"));
        String url = song.getString("url");
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    MainView.this.player.playUrl(url);
                } catch (IOException e) {
                    ContextAPI.makeToast("时间过去太久，链接好像失效了...");
                }
                ContextAPI.makeToast("歌曲正在播放，请稍后...");
            }
        }.start();
        return song.toString();
    }

    public void simplePlay(String jsonStr) {
        Log.i("simplePlay", jsonStr);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Json json = Json.parse(jsonStr);
                String surl = json.getString("surl");
                try {
                    MainView.this.player.playUrl(surl);
                } catch (IOException e) {
                    ContextAPI.makeToast("时间过去太久，链接好像失效了...");
                }
                ContextAPI.makeToast("歌曲正在播放，请稍后...");
            }
        }.start();
    }

    public boolean isPlaying() {
        return this.player.isPlaying();
    }

    public void simpleStop() {
        Log.i("simpleStop", "stop");
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                MainView.this.player.stop();
            }
        });
    }

    /**
     * 简单的播放器
     */
    private class SimplePlayer {
        private MediaPlayer mp = null;
        private String currentUrl = null;

        public SimplePlayer() {
            this.mp = new MediaPlayer();
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mp.start();
                }
            });
        }

        public void playUrl(String url) throws IOException {
            if (mp.isPlaying())
                mp.stop();
            currentUrl = url;
            mp.reset();
            mp.setDataSource(url);
            mp.prepareAsync();
        }

        public void stop() {
            if (mp.isPlaying()) {
                mp.stop();
                currentUrl = null;
            } else {
                ContextAPI.makeToast("当前没有播放音乐");
            }
        }

        public boolean isPlaying() {
            return currentUrl != null;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            PackageManager packageManager = getPackageManager();
            ResolveInfo homeInfo = packageManager.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
            ActivityInfo ai = homeInfo.activityInfo;

            Intent startIntent = new Intent(Intent.ACTION_MAIN);
            startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startIntent.setComponent(new ComponentName(ai.packageName, ai.name));

            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        webView.reload();
//    }
}
