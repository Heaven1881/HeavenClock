package mine.android.api;

import mine.android.api.database.DataBase;
import mine.android.api.modules.Config;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Heaven on 15/7/18
 */
public class ConfigAPI {
    public static Config get() {
        DataBase<Config> db = new DataBase<Config>(Config.class);
        List<Config> configs = db.readAll();
        return configs.size() == 0 ? new Config() : configs.get(0);
    }

    public static void save(Config config) {
        DataBase<Config> db = new DataBase<Config>(Config.class);
        db.replaceAll(Arrays.asList(config));
    }
}
