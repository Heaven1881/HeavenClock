package mine.android.api.modules;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Heaven on 15/12/26
 */
public class Json extends JSONObject implements Serializable {
    public Json(String str) throws JSONException {
        super(str);
    }

    public Json() {
        super();
    }

    public static Json parse(String jsonStr) {
        try {
            return new Json(jsonStr);
        } catch (JSONException e) {
            return new Json();
        }
    }

    public static Json create() {
        return new Json();
    }

    public static Json create(Object... args) {
        Json json = Json.create();
        for (int i = 0; i + 1 < args.length; i += 2) {
            json.put(String.valueOf(args[i]), args[i + 1]);
        }
        return json;
    }

    @Override
    public Json put(String name, boolean value) {
        try {
            super.put(name, value);
        } catch (JSONException ignored) {
        }
        return this;
    }

    @Override
    public Json put(String name, int value) {
        try {
            super.put(name, value);
        } catch (JSONException ignored) {
        }
        return this;
    }

    @Override
    public Json put(String name, double value) {
        try {
            super.put(name, value);
        } catch (JSONException ignored) {
        }
        return this;
    }

    @Override
    public Json put(String name, long value) {
        try {
            super.put(name, value);
        } catch (JSONException ignored) {
        }
        return this;
    }

    @Override
    public Json put(String name, Object value) {
        try {
            if (value instanceof Integer) {
                this.put(name, (int) value);
            } else if (value instanceof Boolean) {
                this.put(name, (boolean) value);
            } else if (value instanceof Double) {
                this.put(name, (double) value);
            } else if (value instanceof Long) {
                this.put(name, (long) value);
            } else if (value instanceof String) {
                this.put(name, (String) value);
            } else {
                super.put(name, value);
            }
        } catch (JSONException ignored) {
        }
        return this;
    }

    public Json put(String name, String value) {
        try {
            super.put(name, value);
        } catch (JSONException ignored) {
        }
        return this;
    }

    @Override
    public boolean getBoolean(String name) {
        try {
            return super.getBoolean(name);
        } catch (JSONException ignored) {
        }
        return false;
    }

    @Override
    public double getDouble(String name) {
        try {
            return super.getDouble(name);
        } catch (JSONException ignored) {
        }
        return 0;
    }

    @Override
    public int getInt(String name) {
        try {
            return super.getInt(name);
        } catch (JSONException ignored) {
        }
        return 0;
    }

    @Override
    public long getLong(String name) {
        try {
            return super.getLong(name);
        } catch (JSONException ignored) {
        }
        return 0;
    }

    public String getString(String name) {
        try {
            return super.getString(name);
        } catch (JSONException ignored) {
        }
        return null;
    }

    public JsonArray getJsonArray(String name) {
        try {
            return JsonArray.parse(super.getJSONArray(name).toString());
        } catch (JSONException e) {
            return null;
        }
    }
}
