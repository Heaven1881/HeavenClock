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

    public static void checkDoubanInfo() {
        new Thread() {
            @Override
            public void run() {
                DoubanAPI.LoginInfo loginInfo = DoubanAPI.getLoginInfo(true);
                if (loginInfo == null) {
                    ContextAPI.makeToast("该账户和密码可能有问题~");
                } else {
                    ContextAPI.makeToast("账户已经通过验证~");
                }
            }
        }.start();
    }
}
