package mine.android.api.modules;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Heaven on 15/12/27
 */
public class JsonArray extends JSONArray implements Serializable, Iterable<Json> {
    private JsonArray() {
        super();
    }

    private JsonArray(String jsonStr) throws JSONException {
        super(jsonStr);
    }

    // TODO 提供其他类型的list方法
    private JsonArray(Collection set) {
        super(set);
    }

    public static JsonArray parse(String jsonStr) {
        try {
            return new JsonArray(jsonStr);
        } catch (JSONException e) {
            return new JsonArray();
        }
    }

    public static JsonArray create() {
        return new JsonArray();
    }

    public static JsonArray create(Collection set) {
        return new JsonArray(set);
    }

    // TODO 提供其他类型的put方法
    public JsonArray put(String str) {
        super.put(str);
        return this;
    }

    public JsonArray put(Json json) {
        super.put(json);
        return this;
    }

    // TODO 提供其他类型的get方法
    public Json getJson(int index) {
        try {
            return Json.parse(super.getJSONObject(index).toString());
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public String getString(int index) {
        try {
            return super.getString(index);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public Iterator<Json> iterator() {
        return new Iterator<Json>() {
            int now = 0;

            @Override
            public boolean hasNext() {
                return now < JsonArray.this.length();
            }

            @Override
            public Json next() {
                return JsonArray.this.getJson(now++);
            }

            @Override
            public void remove() {
            }
        };
    }
}
