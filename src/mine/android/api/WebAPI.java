package mine.android.api;

import org.json.JSONException;

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
}
