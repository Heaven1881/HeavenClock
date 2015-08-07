package mine.android.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import mine.android.HeavenClock.R;
import mine.android.api.AlarmAPI;
import mine.android.api.ContextAPI;
import mine.android.api.DouBanPlayer;
import mine.android.ctrl.ClockCtrl;
import mine.android.ctrl.ConfigCtrl;
import mine.android.ctrl.SongCtrl;

import java.io.IOException;

/**
 * Created by Heaven on 15/7/18
 */
public class MainView extends Activity {
    private static Context context = null;
    private WebView webView;
    private Handler handler = new Handler();
    private MediaPlayer mp = null;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;

        webView = (WebView) findViewById(R.id.mainView);

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mp.start();
            }
        });

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

        //js java 映射
        webView.addJavascriptInterface(new ClockCtrl(handler, webView), "clock");
        webView.addJavascriptInterface(new ConfigCtrl(handler, webView), "config");
        webView.addJavascriptInterface(this, "simplePlayer");
        webView.loadUrl("file:///android_asset/mainView.html");

        AlarmAPI.activeAllClock();
    }

    public void simplePlay(final String url) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mp.reset();
                    mp.setDataSource(url);
                    mp.prepareAsync();
                } catch (IOException ignored) {
                }
                ContextAPI.makeToast("歌曲正在播放，请稍后...");
                Log.i("simple player", "start");
            }
        });
    }

    public void simpleStop() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mp.reset();
                Log.i("simple player", "stop");
            }
        });
    }

    @SuppressWarnings("NullableProblems")
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
}
