package mine.android.api;

import android.util.Log;
import mine.android.api.modules.Json;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by Heaven on 2015/2/15.
 */
public class DoubanAPI {
    /*
    {
        "user_id": "121565834",
        "err": "ok",
        "token": "0c0cc3a2c7",
        "expire": "1466650663",
        "r": 0,
        "user_name": "Heaven",
        "email": "Heaven1881@163.com"
    }
     */

    private static Json loginData = null;

    public static Json login() {
        // 获取配置信息
        Json config = ConfigAPI.get();
        String email = config.getString("douban_email");
        String password = config.getString("douban_password");

        // 如果登陆的用户名和之前的登陆相同，则直接返回
        if (loginData != null) {
            if (loginData.getInt("r") == 0 &&
                    loginData.getString("email").equals(email) &&
                    loginData.getLong("expire") < System.currentTimeMillis() / 1000) {
                return loginData;
            }
        }
        // 否则向服务器登陆
        String loginUrl = "https://www.douban.com/j/app/login";
        String param = String.format("%s=%s&%s=%s&%s=%s&%s=%s",
                "app_name", "radio_android",
                "version", 100,
                "email", email,
                "password", password
        );
        String response = get(loginUrl, param, "POST");
        loginData = Json.parse(response);

        if (loginData.getInt("r") != 0) {
            Log.e("Douban Login", String.format("Can not login to douban, err=%s", loginData.getString("err")));
            throw new DoubanException("Can not login to douban");
        }

        return loginData;
    }

    /**
     * 访问url并获取返回结果
     *
     * @param u      url
     * @param param  访问参数，格式为arg1=a&arg2=b
     * @param method 是否是post方法，如果为false，则使用get方法
     * @return 返回请求结果
     * @throws IOException
     */
    private static String get(String u, String param, String method) {
        try {
            StringBuilder json = new StringBuilder();

            URL url = new URL(u);
            if ("GET".equals(method) && param != null) {
                url = new URL(u + "?" + param);
            }
            URLConnection uc = url.openConnection();
            uc.setDoInput(true);
            uc.setDoOutput(true);

            PrintWriter out = null;
            if (param != null && "POST".equals(method)) {
                out = new PrintWriter(uc.getOutputStream());
                out.print(param);
                out.flush();
            }
            Map<String, List<String>> headerFields = uc.getHeaderFields();

            InputStream inputStream = uc.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader in = new BufferedReader(inputStreamReader);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
            if (out != null)
                out.close();

            return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    /**
     * 报告 向服务器报告
     *
     * @param data data
     * @return json
     */
    public static Json report(Json data) {
        Log.i("DoubanAPI report", data.toString());
        String reportUrl = "https://www.douban.com/j/app/radio/people";
        Json loginResult = login();
        String param = String.format("%s=%s&%s=%s&%s=%s&%s=%s&%s=%s",
                "app_name", "radio_android",
                "version", 100,
                "expire", loginResult.getString("expire"),
                "token", loginResult.getString("token"),
                "user_id", loginResult.getString("user_id")
        );

        if (data.has("channel"))
            param += String.format("&%s=%s", "channel", data.getInt("channel"));
        if (data.has("sid"))
            param += String.format("&%s=%s", "sid", data.getInt("sid"));
        if (data.has("type"))
            param += String.format("&%s=%s", "type", data.getString("type"));

        String response = get(reportUrl, param, "GET");
        Json ret = Json.parse(response);
        if (ret.getInt("r") != 0) {
            throw new DoubanException(ret.getString("err"));
        }
        return ret;
    }

    public static class DoubanException extends RuntimeException {
        String err;
        public DoubanException(String s) {
            err = s;
        }

        @Override
        public String getMessage() {
            return this.err;
        }
    }
}
