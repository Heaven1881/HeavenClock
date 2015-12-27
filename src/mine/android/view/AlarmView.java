package mine.android.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import mine.android.HeavenClock.R;
import mine.android.api.AlarmAPI;
import mine.android.api.ClockAPI;
import mine.android.api.ContextAPI;
import mine.android.api.modules.Json;
import mine.android.ctrl.PlayCtrl;

/**
 * Created by Heaven on 15/7/19
 */
public class AlarmView extends Activity {
    private WebView webView;
    private Handler handler = new Handler();
    private PlayCtrl playCtrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        webView = (WebView) findViewById(R.id.detailView);
        ContextAPI.initWebView(webView);

        Intent intent = getIntent();
        int cid = intent.getIntExtra("cid", -1);
        Json clock = ClockAPI.getClockEntryById(cid);
        this.onClockAlarm(clock);

        playCtrl = new PlayCtrl(handler, webView);
        playCtrl.setClock(clock);

        //js java 映射
        webView.addJavascriptInterface(playCtrl, "PlayCtrl");
        webView.addJavascriptInterface(this, "Activity");
        webView.loadUrl("file:///android_asset/timer.html");

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    AlarmView.this.playCtrl.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 事件处理函数，用于处理一个闹钟成功触发的事件
     *
     * @param clock json
     */
    private void onClockAlarm(Json clock) {
        if (!clock.getBoolean("active"))    // 不可能触发一个没有激活的闹钟
            assert false;
        String type = clock.getString("type");
        int cid = clock.getInt("cid");
        if (type.equals("FOR_ONCE")) { // 闹钟是一次性的，则修改闹钟激活状态
            ClockAPI.setClockEntryActive(cid, false);
        } else {                        // 否则 设置下一次定时器
            long nextAlarm = AlarmAPI.caculateNextAlarm(clock);
            AlarmAPI.setTimer(cid, nextAlarm);
        }
    }

    /**
     * 关闭当前Activity
     */
    public void closeActivity() {
        playCtrl.stop();
        AlarmView.this.finish();
    }
}
