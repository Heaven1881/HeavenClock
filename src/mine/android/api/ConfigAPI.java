package mine.android.api;

import mine.android.HeavenClock.MainActivity;
import mine.android.db.DataBase;
import mine.android.modules.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Heaven on 2015/2/17.
 */
public class ConfigAPI {
    public static Configuration getConfig() {
        DataBase<Configuration> db = new DataBase<Configuration>(Configuration.class, MainActivity.getContext());
        List<Configuration> list = db.readAll();
        if (list.size() > 0) {
            return list.get(0);
        }
        return new Configuration();
    }

    public static void saveConfig(Configuration c) {
        DataBase<Configuration> db = new DataBase<Configuration>(Configuration.class, MainActivity.getContext());
        db.replaceAll(Arrays.asList(c));
    }
}
