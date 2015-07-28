package mine.android.api;

import android.content.Context;
import android.widget.Toast;
import mine.android.view.MainView;

/**
 * Created by Heaven on 15/7/18
 */
public class ContextAPI {
    public static Context get() {
        return MainView.getContext();
    }

    public static void makeToast(String str) {
        Toast.makeText(get(), str, Toast.LENGTH_SHORT).show();
    }
}
