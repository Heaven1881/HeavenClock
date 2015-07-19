package mine.android.ctrl;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import mine.android.api.ClockEntryAPI;
import mine.android.api.modules.ClockEntry;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

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

    private JSONObject clockEntryToJsonObject(ClockEntry clockEntry) {
        Map map = new HashMap();
        map.put("id", clockEntry.getId());
        map.put("name", clockEntry.getName());
        map.put("for", clockEntry.getType());
        map.put("active", clockEntry.isActive());

        int hourOfDay = clockEntry.getHourOfDay();
        int minute = clockEntry.getMinute();
        String time = (hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay)
                + (minute < 10 ? "0" + minute : "" + minute);
        map.put("time", time);

        boolean[] week = new boolean[7];
        for (int i = 0; i < 7; i++) {
            week[i] = clockEntry.weeks(i);
        }
        map.put("week", week);

        return new JSONObject(map);
    }

    /**
     * 获取闹钟信息
     *
     * @param id       闹钟id
     * @param callback 回调函数
     */
    public void getClockDetail(String id, final String callback) {
        Log.i("getClockDetail", "id=" + Integer.parseInt(id));

        ClockEntry clockEntry = ClockEntryAPI.getById(Integer.parseInt(id));
        final String jsonStr = clockEntryToJsonObject(clockEntry).toString();

        handler.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:" + callback + "('" + jsonStr + "')");
            }
        });
    }

    /**
     * 删除对应id的entry
     *
     * @param idStr id
     */
    public void deleteClockById(String idStr) {
        int id = Integer.parseInt(idStr);
        ClockEntryAPI.deleteClockEntry(id);
    }

    /**
     * 添加clock entry, 若id==0则表示添加，否则为更新
     * @param hourStr 小时
     * @param minuteStr 分钟
     * @param typeStr 闹钟类别
     * @param weekStr 自定义标记
     * @param nameStr 备注
     */
    public void updateClockEntry(String idStr, String hourStr, String minuteStr, String typeStr,
                              String weekStr, String nameStr) {
        int id = Integer.parseInt(idStr);
        int hour = Integer.parseInt(hourStr);
        int minute = Integer.parseInt(minuteStr);
        int week = Integer.parseInt(weekStr);

        ClockEntry entry = new ClockEntry();
        entry.setHourOfDay(hour);
        entry.setMinute(minute);
        if ("once".equals(typeStr)) {
            entry.setType(ClockEntry.ClockType.FOR_ONCE);
        } else if ("day".equals(typeStr)) {
            entry.setType(ClockEntry.ClockType.FOR_DAY);
        } else if ("week".equals(typeStr)) {
            entry.setType(ClockEntry.ClockType.FOR_WEEK);
        }
        entry.setWeeks(week);
        entry.setName(nameStr);

        if (id == 0) {
            ClockEntryAPI.addClockEntry(entry);
        } else {
            ClockEntryAPI.updateClockEntry(id, entry);
        }
    }

    /**
     * 获取所有clock列表
     * @param callback 回调函数
     */
    public void getAllClockEntry(final String callback) {
        List<ClockEntry> clockEntries = ClockEntryAPI.get();
        Collections.sort(clockEntries);

        List jsons = new ArrayList();
        for (ClockEntry entry : clockEntries) {
            JSONObject json = clockEntryToJsonObject(entry);
            jsons.add(json);
        }
        JSONArray jsonArray = new JSONArray(jsons);
        final String arrayStr = jsonArray.toString();
        handler.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:" + callback + "('" + arrayStr + "')");
            }
        });
    }
}
