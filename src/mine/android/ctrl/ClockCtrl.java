package mine.android.ctrl;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import mine.android.api.AlarmAPI;
import mine.android.api.ClockAPI;
import mine.android.api.SongHistoryAPI;
import mine.android.api.modules.Json;
import mine.android.api.modules.JsonArray;

/**
 * Created by Heaven on 15/7/19
 */
public class ClockCtrl {
    private WebView webView = null;
    private Handler handler = null;

    public ClockCtrl(Handler handler, WebView webView) {
        this.webView = webView;
        this.handler = handler;
    }

    /**
     * 获取闹钟信息
     *
     * @param jsonStr data
     */
    public String getClockEntry(String jsonStr) {
        Json json = Json.parse(jsonStr);
        int cid = json.getInt("cid");
        String entryStr = ClockAPI.getClockEntryById(cid).toString();
        Log.i("getClockEntry", entryStr);
        return entryStr;
    }

    /**
     * 删除clock entry
     *
     * @param jsonStr cid
     */
    public void deleteClockEntry(String jsonStr) {
        Json json = Json.parse(jsonStr);
        int cid = json.getInt("cid");
        AlarmAPI.cancelTimerByClockId(cid);
        ClockAPI.deleteClockEntryById(cid);
        Log.i("deleteClockEntry", jsonStr);
    }

    /**
     * 修改clock entry 若cid==0则表示添加
     *
     * @param jsonStr json str
     */
    public void setClockEntry(String jsonStr) {
        Json clock = Json.parse(jsonStr);
        int cid = ClockAPI.setClockEntry(clock);
        Log.i("set clock", String.format("[cid=%d] %s", cid, jsonStr));
        if (clock.getBoolean("active"))
            AlarmAPI.setTimerByClockId(cid);
    }

    /**
     * 获取所有clock列表
     */
    public String getClockEntries() {
        JsonArray array = ClockAPI.getClockEntries();
        Json json = Json.create(
                "clock_entries", array
        );
        String jsonStr = json.toString();
        Log.i("getClockEntries", jsonStr);
        return jsonStr;
    }

    /**
     * 激活闹钟
     *
     * @param jsonStr data
     */
    public void activeClock(String jsonStr) {
        Json json = Json.parse(jsonStr);
        int cid = json.getInt("cid");
        boolean active = json.getBoolean("active");
        if (active) {
            AlarmAPI.setTimerByClockId(cid);
        } else {
            AlarmAPI.cancelTimerByClockId(cid);
        }
        ClockAPI.setClockEntryActive(cid, active);
        Log.i("activeClock", jsonStr);
    }

    public String getHistory() throws Exception {
        JsonArray array = SongHistoryAPI.getSongEntries();
        Json json = Json.create(
                "song_history", array
        );
        Log.i("getHistory", String.format("length = %d", array.length()));
        return json.toString();
    }
}
