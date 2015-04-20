package mine.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Heaven on 2015/2/10.
 */
public class DataBase<T extends Serializable & Comparable<T>> {
    private static final String EXT = "dat";
    private File dbPath = null;
    private Context context = null;

    private List<T> cache = null;

    public DataBase(Class aClass, Context con) {
        context = con;
        dbPath = new File(context.getFilesDir(), aClass.getSimpleName() + "." + EXT);
    }

    /**
     * addItem list to database
     *
     * @param list List to addItem
     */
    public void addItem(List<T> list) {
        if (list == null)
            throw new IllegalArgumentException("list cannot be null");

        List<T> oldList = getFromDB();

        for (T t : list) {
            if (oldList.contains(t))
                continue;
            oldList.add(t);
            Log.d("DataBase", "insert : " + t.toString());
        }

        putToDB(oldList);
    }

    public List<T> readAll() {
        return getFromDB();
    }

    public void replaceAll(List<T> list) {
        putToDB(list);
    }

    private synchronized List<T> getFromDB() {
        if (cache != null)
            return  cache;
        cache = new ArrayList<T>();

        if (!dbPath.exists())
            return cache;

        try {
            FileInputStream fis = context.openFileInput(dbPath.getName());
            ObjectInputStream ois = new ObjectInputStream(fis);
            cache = (List<T>) ois.readObject();

            ois.close();
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return cache;
    }

    private synchronized void putToDB(List<T> list) {
        Collections.sort(list);
        try {
            FileOutputStream fos = context.openFileOutput(dbPath.getName(), Context.MODE_WORLD_WRITEABLE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(list);

            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
