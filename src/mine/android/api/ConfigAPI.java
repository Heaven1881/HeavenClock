package mine.android.api;

import mine.android.api.database.DataManager;
import mine.android.api.modules.Json;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Heaven on 15/7/18
 */
public class ConfigAPI {
    /*
    {
        "max_history": 15,
        "p_for_new": 0.5,
        "douban_password": "xxxxxxxxxx",
        "douban_email": "xxxxxxxx@163.com",
        "repeat": 3
    }
     */
    private static Json config = null;
    private static Json defaultConfig = Json.create(
            "max_history", 10,
            "p_for_new", 0.5,
            "repeat", 3
    );

    private static Json loadData() {
        if (config != null)
            return config;
        synchronized ("Config") {
            if (config != null)
                return config;
            DataManager dataManager = new DataManager("Config");
            config = dataManager.getJson(defaultConfig);
        }
        return config;
    }

    private static void saveData() {
        DataManager dataManager = new DataManager("Config");
        dataManager.save(config);
    }

    /**
     * 获取数据
     *
     * @return json
     */
    public static Json get() {
        return loadData();
    }

    /**
     * 保存数据
     *
     * @param config json
     */
    public static void save(Json config) {
        ConfigAPI.config = config;
        saveData();
    }
}
