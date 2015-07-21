package mine.android.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import mine.android.HeavenClock.R;
import mine.android.api.AlarmAPI;
import mine.android.api.ClockEntryAPI;
import mine.android.api.ConfigAPI;
import mine.android.api.DouBanPlayer;
import mine.android.api.modules.ClockEntry;
import mine.android.api.modules.Config;
import mine.android.api.modules.Song;
import mine.android.ctrl.ClockCtrl;
import mine.android.ctrl.SongCtrl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Heaven on 15/7/19
 */
public class AlarmView extends Activity {
    private WebView webView;
    private Handler handler = new Handler();
    private DouBanPlayer player = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        webView = (WebView) findViewById(R.id.detailView);

        // 初始化播放器
        ExecutorService pool = Executors.newSingleThreadExecutor();
        Config config = ConfigAPI.get();
        player = new DouBanPlayer(config.getRepeatSong(), config.getpForNew(), pool);


        // WebView 设置
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        // 设置加载过程显示的进入信息
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading...");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressDialog.show();
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressDialog.setMessage(newProgress + "%");
            }
        });

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        ClockEntry entry = ClockEntryAPI.getById(id);
        if (entry.getType() == ClockEntry.ClockType.FOR_ONCE) {
            AlarmAPI.cancelClock(id);
        }

        SongCtrl songCtrl = new SongCtrl(handler, webView, player);
        songCtrl.setEntry(entry);

        //js java 映射
        webView.addJavascriptInterface(songCtrl, "song");
        webView.addJavascriptInterface(this, "activity");
        webView.loadUrl("file:///android_asset/alarmView.html");

        Log.i("AlarmActivity", "STARTED");

        new Thread(){
            @Override
            public void run() {
                player.start();
            }
        }.start();

    }

    public void stop() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                player.stop();
                AlarmView.this.finish();
            }
        });
    }
}
