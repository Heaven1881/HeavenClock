package mine.android.ctrl;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import mine.android.api.ConfigAPI;
import mine.android.api.ContextAPI;
import mine.android.api.modules.Json;

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
     * 获取设置信息
     * @return data
     */
    public String getSettings() throws Exception{
        String jsonStr = ConfigAPI.get().toString();
        Log.i("getSettings", jsonStr);
        return jsonStr;
    }

    /**
     * 修改设置
     * @param jsonStr 包含要修改的字段
     */
    public void setSettings(String jsonStr) throws Exception{
        Json config = Json.parse(jsonStr);
        ConfigAPI.save(config);

        String line = "账户信息已保存";
        ContextAPI.makeToast(line);
        Log.i("update config", jsonStr);
    }
}
