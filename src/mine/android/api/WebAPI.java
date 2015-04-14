package mine.android.api;

import android.util.Log;
import mine.android.HeavenClock.MainActivity;
import mine.android.HeavenClock.R;
import mine.android.modules.ClockSong;
import mine.android.modules.Configuration;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Heaven on 2015/2/15.
 */
public class WebAPI {
    public static final char OP_SKIP = 's';
    public static final char OP_GET_NEW_LIST = 'n';
    public static final char OP_GET_NEXT_SONG = 's';

    public static final char OP_MARK_AS_LIKE = 'r';
    public static final char OP_DMARK_LIKE = 'u';
    public static final char OP_BYE = 'b';
    public static final char OP_END = 'e';


    public static String getStringFromUrl(String u, String param, boolean post) throws IOException, JSONException {
        StringBuilder json = new StringBuilder();

        URL url = new URL(u);
        if (!post && param != null) {
            url = new URL(u + "?" + param);
        }
        URLConnection uc = url.openConnection();
        uc.setDoInput(true);
        uc.setDoOutput(true);

        PrintWriter out = null;
        if (param != null && post) {
            out = new PrintWriter(uc.getOutputStream());
            out.print(param);
            out.flush();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            json.append(inputLine);
        }
        in.close();
        if (out != null)
            out.close();

        return json.toString();
    }

    public static LogInfo loginToDouban(String email, String password) throws IOException {
        String url = MainActivity.getContext().getString(R.string.login_url);
        StringBuilder param = new StringBuilder();
        LogInfo logInfo = new LogInfo();
        try {
            param.append("app_name=" + "radio_desktop_win" + "&");
            param.append("version=" + 100 + "&");
            param.append("email=" + email + "&");
            param.append("password=" + password);

            String retStr = getStringFromUrl(url, param.toString(), true);
            JSONObject logRet = new JSONObject(retStr);
            if (logRet.getInt("r") != 0) {
                Log.e("login error", "can not login to douban");
                return logInfo;
            }
            logInfo.expire = logRet.getString("expire");
            logInfo.token = logRet.getString("token");
            logInfo.userId = logRet.getString("user_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return logInfo;
    }

    public static List<ClockSong> SongListOperation(int chanel, char type) {
        return SongListOperation(chanel, type, 0);
    }

    public static List<ClockSong> SongListOperation(int channel, char type, int sid) {
        Log.i("song oper", "channel=" + channel + " type=" + type + " songId=" + sid);
        Configuration c = ConfigAPI.getConfig();
        String email = c.getDoubanEmail();
        String password = c.getDoubanPassword();
        String url = MainActivity.getContext().getString(R.string.get_list_url);
        List<ClockSong> retList = new ArrayList<ClockSong>();
        LogInfo logInfo;
        try {
            logInfo = loginToDouban(email, password);

            StringBuilder param = new StringBuilder();
            param.append("app_name=" + "radio_desktop_win" + "&");
            param.append("version=" + 100 + "&");
            param.append("user_id=" + logInfo.userId + "&");
            param.append("expire=" + logInfo.expire + "&");
            param.append("token=" + logInfo.token + "&");
            param.append("channel=" + channel + "&");
            param.append("type=" + type + "&");
            param.append("sid=" + sid);

            String listStr = getStringFromUrl(url, param.toString(), true);
            JSONObject response = new JSONObject(listStr);
            if (response.getInt("r") != 0) {
                return retList;
            }
            JSONArray songList = response.getJSONArray("song");
            for (int i = 0; i < songList.length(); i++) {
                JSONObject song = (JSONObject) songList.get(i);
                String songUrl = song.getString("url");

                ClockSong clockSong = new ClockSong(songUrl);
                clockSong.setTitle(song.getString("title"));
                clockSong.setArtist(song.getString("artist"));
                clockSong.setSid(song.getInt("sid"));
                clockSong.setLike(song.getInt("like") == 1 ? true : false);

                retList.add(clockSong);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return retList;
    }

    private static class LogInfo {
        String userId;
        String token;
        String expire;
    }
}
