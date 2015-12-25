package mine.android.ctrl;

import android.os.Handler;
import android.util.Log;
import android.webkit.WebView;
import mine.android.api.AlarmAPI;
import mine.android.api.ClockEntryAPI;
import mine.android.api.SongListAPI;
import mine.android.api.modules.ClockEntry;
import mine.android.api.modules.Song;
import org.json.JSONArray;
import org.json.JSONException;
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

    public static String formatTime(int hourOfDay, int minute) {
        return (hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay) + ":"
                + (minute < 10 ? "0" + minute : "" + minute);
    }

    private JSONObject clockEntryToJsonObject(ClockEntry clockEntry) {
        JSONObject json = new JSONObject();
        try {
            json.put("cid", clockEntry.getId());
            json.put("name", clockEntry.getName());
            json.put("for", clockEntry.getType());
            json.put("active", clockEntry.isActive());
            json.put("time", formatTime(clockEntry.getHourOfDay(), clockEntry.getMinute()));
            json.put("week", clockEntry.getWeeks());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 获取闹钟信息
     * @param jsonStr data
     */
    public String getClockEntry(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            int cid = json.getInt("cid");
            ClockEntry entry = ClockEntryAPI.getById(cid);
            String entryStr = clockEntryToJsonObject(entry).toString();
            Log.i("getClockEntry", entryStr);
            return entryStr;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{}";
    }

    /**
     * 获取闹钟信息
     *
     * @param id       闹钟id
     * @param callback 回调函数
     */
    @Deprecated
    public void getClockDetail(final String id, final String callback) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                ClockEntry clockEntry = ClockEntryAPI.getById(Integer.parseInt(id));
                final String jsonStr = clockEntryToJsonObject(clockEntry).toString();

                Log.i("req clockEntry", jsonStr);
                webView.loadUrl("javascript:" + callback + "('" + jsonStr + "')");
            }
        });
    }

    /**
     * 删除clock entry
     * @param jsonStr cid
     */
    public void deleteClockEntry(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            int cid = json.getInt("cid");
            AlarmAPI.cancelClock(cid);
            ClockEntryAPI.deleteClockEntry(cid);
            Log.i("deleteClockEntry", jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除对应id的entry
     *
     * @param idStr id
     */
    @Deprecated
    public void deleteClockById(final String idStr) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                int id = Integer.parseInt(idStr);
                AlarmAPI.cancelClock(id);
                ClockEntryAPI.deleteClockEntry(id);
                Log.i("delete entry", idStr);
                webView.loadUrl("javascript:simpleUpdate('clock')");
            }
        });
    }

    /**
     * 修改clock entry 若cid==0则表示添加
     *
     * @param jsonStr json str
     */
    public void setClockEntry(String jsonStr) {
        try {
            Log.i("debug", jsonStr);
            JSONObject json = new JSONObject(jsonStr);
            int cid = json.getInt("cid");
            ClockEntry entry = new ClockEntry();
            entry.setId(json.getInt("cid"));
            entry.setHourOfDay(Integer.parseInt(json.getString("time").split(":")[0]));
            entry.setMinute(Integer.parseInt(json.getString("time").split(":")[1]));
            String typeStr = json.getString("repeat");
            if ("FOR_ONCE".equals(typeStr)) {
                entry.setType(ClockEntry.ClockType.FOR_ONCE);
            } else if ("FOR_DAY".equals(typeStr)) {
                entry.setType(ClockEntry.ClockType.FOR_DAY);
            } else if ("FOR_WEEK".equals(typeStr)) {
                entry.setType(ClockEntry.ClockType.FOR_WEEK);
            }
            entry.setName(json.getString("desc"));

            int result;
            if (cid == 0) {
                result = ClockEntryAPI.addClockEntry(entry);
                AlarmAPI.activeClock(result);
                Log.i("add entry " + result, jsonStr);
            } else {
                result = ClockEntryAPI.updateClockEntry(entry);
                AlarmAPI.activeClock(result);
                Log.i("update entry " + result, jsonStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加clock entry, 若id==0则表示添加，否则为更新
     *
     * @param hourStr   小时
     * @param minuteStr 分钟
     * @param typeStr   闹钟类别
     * @param weekStr   自定义标记
     * @param nameStr   备注
     */
    @Deprecated
    public void updateClockEntry(final String idStr, final String hourStr, final String minuteStr, final String typeStr,
                                 final String weekStr, final String nameStr) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                int id = Integer.parseInt(idStr);
                int hour = Integer.parseInt(hourStr);
                int minute = Integer.parseInt(minuteStr);

                ClockEntry entry = new ClockEntry();
                entry.setId(id);
                entry.setHourOfDay(hour);
                entry.setMinute(minute);
                if ("once".equals(typeStr)) {
                    entry.setType(ClockEntry.ClockType.FOR_ONCE);
                } else if ("day".equals(typeStr)) {
                    entry.setType(ClockEntry.ClockType.FOR_DAY);
                } else if ("week".equals(typeStr)) {
                    entry.setType(ClockEntry.ClockType.FOR_WEEK);
                }
                String[] sl = weekStr.split(",");
                String buf = "";
                for (String s : sl) {
                    buf += s;
                }
                entry.setWeeks(buf);
                entry.setName(nameStr);

                int result = -1;
                if (id == 0) {
                    result = ClockEntryAPI.addClockEntry(entry);
                    AlarmAPI.activeClock(result);
                    Log.i("add entry " + result, idStr + " " + hourStr + " " + minuteStr + " " + typeStr + " " + buf + " " + nameStr);
                } else {
                    result = ClockEntryAPI.updateClockEntry(entry);
                    AlarmAPI.activeClock(result);
                    Log.i("update entry " + result, idStr + " " + hourStr + " " + minuteStr + " " + typeStr + " " + buf + " " + nameStr);
                }
                webView.loadUrl("javascript:simpleUpdate('clock')");
            }
        });
    }

    /**
     * 获取所有clock列表
     */
    public String getClockEntries() {
        List<ClockEntry> clockEntries = ClockEntryAPI.get();

        JSONObject jsons = new JSONObject();
        JSONArray ja = new JSONArray();
        try {
            for (ClockEntry entry : clockEntries) {
                JSONObject json = clockEntryToJsonObject(entry);
                ja.put(json);
            }
            jsons.put("clock_entries", ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonStr = jsons.toString();
        Log.i("getClockEntries", jsonStr);
        return jsonStr;
    }

    /**
     * 激活闹钟
     * @param jsonStr data
     */
    public void activeClock(String jsonStr) {
        try {
            JSONObject json = new JSONObject(jsonStr);
            int cid = json.getInt("cid");
            ClockEntry entry = ClockEntryAPI.getById(cid);
            if (entry == null) {
                Log.i("active false", jsonStr);
            } else {
                if (json.getBoolean("active")) {
                    AlarmAPI.activeClock(cid);
                } else {
                    AlarmAPI.cancelClock(cid);
                }
                ClockEntryAPI.updateField(cid, ClockEntryAPI.FIELD_ACTIVE, json.getBoolean("active"));
                Log.i("active success", jsonStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 激活指定id的闹钟
     *
     * @param idStr id
     * @param sel   status
     */
    @Deprecated
    public void activeClock(final String idStr, final String sel) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                int id = Integer.parseInt(idStr);
                ClockEntry entry = ClockEntryAPI.getById(id);
                if (entry == null) {
                    Log.i("active false", idStr + ":" + sel);
                    return;
                } else {
                    boolean on = sel.equals("on");
                    Log.i("active true", idStr + ":" + sel);
                    if (on) {
                        AlarmAPI.activeClock(id);
                        ClockEntryAPI.updateField(id, ClockEntryAPI.FIELD_ACTIVE, true);
                    } else {
                        AlarmAPI.cancelClock(id);
                        ClockEntryAPI.updateField(id, ClockEntryAPI.FIELD_ACTIVE, false);
                    }
                }
            }
        });
    }

    public String getHistory() {
        SongListAPI.deleteExtraSong();
        List<Song> songs = SongListAPI.get();

        JSONObject jsons = new JSONObject();
        JSONArray ja = new JSONArray();
        try {
            for (Song song : songs) {
                JSONObject json = new JSONObject();
                json.put("url", song.getUrl());
                json.put("name", song.getTitle());
                json.put("artist", song.getArtist());
                json.put("playedTime", song.getPlayTime().getTime());
                json.put("sid", song.getSid());
                ja.put(json);
            }
            jsons.put("song_history", ja);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonStr = jsons.toString();
        Log.i("getHistory", jsonStr);
        return jsonStr;
    }
}
