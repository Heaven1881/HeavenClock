package mine.android.ctrl;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import mine.android.api.ConfigAPI;
import mine.android.api.ContextAPI;
import mine.android.api.modules.Config;
import org.json.JSONException;
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

                JSONObject json = new JSONObject();
                try {
                    json.put("email", config.getDoubanEmail());
                    json.put("pwd", config.getDoubanPassword());
                    json.put("repeatTime", config.getRepeatSong());
                    json.put("pForNew", config.getpForNew());
                    json.put("historySong", config.getHistorySong());
                } catch (JSONException ignored) {
                }

                final String jsonStr = json.toString();
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

                String line = "账户信息已保存";
                ContextAPI.makeToast(line);
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
    public void updateSetting(final String repeatTimeStr, final String pForNewStr, final String historySong) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Config config = ConfigAPI.get();
                config.setRepeatSong(Integer.parseInt(repeatTimeStr));
                config.setpForNew(Double.parseDouble(pForNewStr));
                config.setHistorySong(Integer.parseInt(historySong));
                ConfigAPI.save(config);
                String line = "闹钟设置已保存";
                ContextAPI.makeToast(line);
                Log.i("update setting", repeatTimeStr + ":" + pForNewStr + ":" + historySong);
            }
        });
    }
}
