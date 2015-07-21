package mine.android.ctrl;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import mine.android.api.ConfigAPI;
import mine.android.api.modules.Config;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Heaven on 15/7/19
 */
public class ConfigCtrl {
    private WebView webView = null;
    private Handler handler = null;

    public ConfigCtrl(Handler handler, WebView webView) {
        this.webView = webView;
        this.handler = handler;
    }

    /**
     * 获取设置
     *
     * @param callback
     */
    public void getSetting(final String callback) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Config config = ConfigAPI.get();

                Map map = new HashMap();
                map.put("email", config.getDoubanEmail());
                map.put("pwd", config.getDoubanPassword());
                map.put("repeatTime", config.getRepeatSong());
                map.put("pForNew", config.getpForNew());

                final String jsonStr = new JSONObject(map).toString();
                Log.i("req setting", jsonStr);
                webView.loadUrl("javascript:" + callback + "('" + jsonStr + "')");
            }
        });
    }

    /**
     * 更新douban账户信息
     *
     * @param email email
     * @param pwd   password
     */
    public void updateDouban(final String email, final String pwd) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Config config = ConfigAPI.get();
                config.setDoubanEmail(email);
                config.setDoubanPassword(pwd);
                ConfigAPI.save(config);
                Log.i("update douban", email + ":" + pwd);
            }
        });
    }

    /**
     * 更新闹钟设置信息
     *
     * @param repeatTimeStr 重复此时
     * @param pForNewStr    新歌概率
     */
    public void updateSetting(final String repeatTimeStr, final String pForNewStr) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Config config = ConfigAPI.get();
                config.setRepeatSong(Integer.parseInt(repeatTimeStr));
                config.setpForNew(Double.parseDouble(pForNewStr));
                ConfigAPI.save(config);
                Log.i("update setting", repeatTimeStr + ":" + pForNewStr);
            }
        });
    }
}
