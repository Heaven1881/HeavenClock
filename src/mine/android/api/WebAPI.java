package mine.android.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Heaven on 2015/2/15.
 */
public class WebAPI {

    public static JSONObject getJsonFormUrl(String u) throws IOException, JSONException {
        StringBuilder json = new StringBuilder();

        URL url = new URL(u);
        URLConnection uc = url.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        String inputLine;
        while ( (inputLine = in.readLine()) != null) {
            json.append(inputLine);
        }
        in.close();

        return new JSONObject(json.toString());
    }
}
