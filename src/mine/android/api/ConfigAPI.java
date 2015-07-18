package mine.android.api;

import mine.android.api.database.DataBase;
import mine.android.api.modules.Config;

import java.util.Arrays;

/**
 * Created by Heaven on 15/7/18
 */
public class ConfigAPI {
    public static Config get() {
        DataBase<Config> db = new DataBase<Config>(Config.class);
        Config config = db.readAll().get(0);
        return config == null ? new Config() : config;
    }

    public static void save(Config config) {
        DataBase<Config> db = new DataBase<Config>(Config.class);
        db.replaceAll(Arrays.asList(config));
    }
}
