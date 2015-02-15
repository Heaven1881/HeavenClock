package mine.android.api;

import mine.android.HeavenClock.MainActivity;
import mine.android.HeavenClock.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Heaven on 2015/2/15.
 */
public class WebAPI {

    public static String getStringFromUrl(String u, String param, boolean post) throws IOException, JSONException {
        StringBuilder json = new StringBuilder();

        URL url = new URL(u);
        if (!post && param!=null) {
            url = new URL(u + "?" + param);
        }
        URLConnection uc = url.openConnection();
        uc.setDoInput(true);
        uc.setDoOutput(true);

        if (param != null && post) {
            PrintWriter out = new PrintWriter(uc.getOutputStream());
            out.print(param);
            out.flush();
            out.close();
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String inputLine;
        while ( (inputLine = in.readLine()) != null) {
            json.append(inputLine);
        }
        in.close();

        return json.toString();
    }

    public static LogInfo loginToDouban(String email, String password) {
        String url = MainActivity.getContext().getString(R.string.login_url);
        JSONObject param = new JSONObject();
        try {
            param.put("app_name", "radio_desktop_win");
            param.put("version", 100);
            param.put("email", MainActivity.getContext().getString(R.string.douban_email));
            param.put("password", MainActivity.getContext().getString(R.string.douban_password));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class LogInfo {
        String userId;
        String token;
        String expire;
    }
}
