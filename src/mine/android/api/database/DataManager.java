package mine.android.api.database;

import android.content.Context;
import mine.android.api.ContextAPI;
import mine.android.api.modules.Json;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Heaven on 15/12/25
 * 这个类和database相似，但是只负责保存json
 */
public class DataManager {
    private File storeFile = null;
    private String type = "default";

    public DataManager(String type) {
        this.type = type;
        String filename = String.format("DataManage.%s.json", this.type);
        storeFile = new File(ContextAPI.get().getFilesDir(), filename);
    }

    /**
     * 从文件获取map数据
     *
     * @return Json
     */
    public TreeMap<Integer, Json> getMap() {
        if (!storeFile.exists())
            return new TreeMap<>();
        try {
            FileInputStream fis = ContextAPI.get().openFileInput(storeFile.getName());
            ObjectInputStream ois = new ObjectInputStream(fis);
            TreeMap<Integer, String> mapStr = (TreeMap<Integer, String>) ois.readObject();
            TreeMap<Integer, Json> map = new TreeMap<>();
            for (Map.Entry<Integer, String> entry : mapStr.entrySet()) {
                map.put(entry.getKey(), Json.parse(entry.getValue()));
            }
            ois.close();
            fis.close();
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new TreeMap<>();
    }

    /**
     * 从文件获取json数据
     *
     * @param data 文件不存在时返回的数据
     * @return JSONObject
     */
    public Json getJson(Json data) {
        if (!storeFile.exists())
            return data;
        try {
            FileInputStream fis = ContextAPI.get().openFileInput(storeFile.getName());
            ObjectInputStream ois = new ObjectInputStream(fis);
            String jsonStr = (String) ois.readObject();
            ois.close();
            fis.close();
            return Json.parse(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    /**
     * 将json写入文件
     *
     * @param json json
     */
    public void save(Json json) {
        synchronized (type.intern()) {
            try {
                FileOutputStream fos = ContextAPI.get().openFileOutput(storeFile.getName(), Context.MODE_WORLD_WRITEABLE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(json.toString());
                oos.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将map写入文件
     *
     * @param map map
     */
    public void save(TreeMap<Integer, Json> map) {
        synchronized (type.intern()) {
            try {
                FileOutputStream fos = ContextAPI.get().openFileOutput(storeFile.getName(), Context.MODE_WORLD_WRITEABLE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                TreeMap<Integer, String> mapStr = new TreeMap<>();
                for (Map.Entry<Integer, Json> entry : map.entrySet()) {
                    mapStr.put(entry.getKey(), entry.getValue().toString());
                }
                oos.writeObject(mapStr);
                oos.close();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
