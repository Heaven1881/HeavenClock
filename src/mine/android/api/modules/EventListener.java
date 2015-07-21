package mine.android.api.modules;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Heaven on 15/7/21
 */
@Deprecated
public class EventListener {
    private static Map<String, Map<Object, Method>> methodMap = null;

    private synchronized static void init() {
        if (methodMap != null)
            return;
        methodMap = new HashMap<String, Map<Object, Method>>();
    }

    /**
     * 注册时间，
     * method必须是不带参数的
     *
     * @param obj    obj
     * @param key    keyStr
     * @param method method 无参数
     */
    public static void registerEvent(Object obj, String key, Method method) {
        init();

        // 检查对应的key是否存在
        if (!methodMap.containsKey(key)) {
            Map<Object, Method> map = new HashMap<Object, Method>();
            methodMap.put(key, map);
        }

        methodMap.get(key).put(obj, method);
        Log.i("Register Event: " + key, method.getName());
    }

    public static void emitEvent(String key, Object...args) {
        init();

        if (!methodMap.containsKey(key))
            return;

        // 出发事件对应的函数
        Map<Object, Method> objectMethodMap = methodMap.get(key);
        for (Map.Entry<Object, Method> entry : objectMethodMap.entrySet()) {
            try {
                entry.getValue().invoke(entry.getKey(), args);
            } catch (Exception e) {
                Log.e("invoke error", e.getLocalizedMessage());
            }
        }
    }
}
