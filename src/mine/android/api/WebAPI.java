package mine.android.api;

import android.util.Log;
import mine.android.HeavenClock.R;
import mine.android.api.modules.Config;
import mine.android.api.modules.Song;
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
    public static LoginInfo loginInfo = null;
    public static final char OP_SKIP = 's';
    public static final char OP_GET_NEW_LIST = 'n';
    public static final char OP_GET_NEXT_SONG = 's';

    public static final char OP_MARK_AS_LIKE = 'r';
    public static final char OP_DMARK_LIKE = 'u';
    public static final char OP_BYE = 'b';
    public static final char OP_END = 'e';

    public static LoginInfo getLoginInfo() {
        if (loginInfo != null)
            return loginInfo;
        synchronized (loginInfo) {
            if (loginInfo != null)
                return loginInfo;

            Config config = ConfigAPI.get();
            try {
                loginInfo = loginToDouban(config.getDoubanEmail(), config.getDoubanPassword());
            } catch (IOException ignored) {
            }
            return loginInfo;
        }
    }

    /**
     * 访问url并获取返回结果
     *
     * @param u     url
     * @param param 访问参数，格式为arg1=a&arg2=b
     * @param post  是否是post方法，如果为false，则使用get方法
     * @return 返回请求结果
     * @throws IOException
     */
    public static String get(String u, String param, boolean post) throws IOException {
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

    /**
     * 登录到豆瓣，并返回登录token
     *
     * @param email    豆瓣用户名（邮箱）
     * @param password 密码
     * @return 登录信息
     * @throws IOException
     */
    private static LoginInfo loginToDouban(String email, String password) throws IOException {
        String url = ContextAPI.get().getString(R.string.login_url);
        LoginInfo loginInfo = new LoginInfo();

        StringBuilder param = new StringBuilder();
        try {
            param.append("app_name=" + "radio_desktop_win" + "&");
            param.append("version=" + 100 + "&");
            param.append("email=").append(email).append("&");
            param.append("password=").append(password);

            String retStr = get(url, param.toString(), true);
            JSONObject logRet = new JSONObject(retStr);
            if (logRet.getInt("r") != 0) {
                Log.e("login error", "can not login to douban");
                return loginInfo;
            }
            loginInfo.expire = logRet.getString("expire");
            loginInfo.token = logRet.getString("token");
            loginInfo.userId = logRet.getString("user_id");
        } catch (JSONException ignored) {
        }
        return loginInfo;
    }

    public static List<Song> getNewList(int channel) {
        return remoteOperation(channel, WebAPI.OP_GET_NEW_LIST, 0);
    }

    public static List<Song> remoteOperation(int channel, char type, int sid) {
        Log.i("song oper", "channel=" + channel + " type=" + type + " songId=" + sid);

        String url = ContextAPI.get().getString(R.string.get_list_url);
        List<Song> retList = new ArrayList<Song>();
        try {
            LoginInfo loginInfo = getLoginInfo();

            String result = get(url,
                    ("app_name=" + "radio_desktop_win" + "&")
                            + "version=" + 100 + "&" + "user_id=" + loginInfo.userId + "&"
                            + "expire=" + loginInfo.expire + "&"
                            + "token=" + loginInfo.token + "&"
                            + "channel=" + channel + "&"
                            + "type=" + type + "&"
                            + "sid=" + sid,
                    true);

            JSONObject response = new JSONObject(result);
            if (response.getInt("r") != 0) {
                return retList;
            }

            JSONArray songList = response.getJSONArray("song");
            for (int i = 0; i < songList.length(); i++) {
                JSONObject song = (JSONObject) songList.get(i);
                String songUrl = song.getString("url");

                Song clockSong = new Song(songUrl);
                clockSong.setTitle(song.getString("title"));
                clockSong.setArtist(song.getString("artist"));
                clockSong.setSid(song.getInt("sid"));
                clockSong.setLike(song.getInt("like") == 1);

                retList.add(clockSong);
            }

        } catch (IOException ignored) {
        } catch (JSONException ignored) {
        }
        return retList;
    }

    private static class LoginInfo {
        String userId;
        String token;
        String expire;
    }
}
