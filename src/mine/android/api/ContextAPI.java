package mine.android.api;

import android.content.Context;
import mine.android.view.MainView;

/**
 * Created by Heaven on 15/7/18
 */
public class ContextAPI {
    public static Context get() {
        return MainView.getContext();
    }
}
