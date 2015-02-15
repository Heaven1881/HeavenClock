package mine.android.HeavenClock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Heaven on 2015/2/12.
 */
public class AlarmActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        boolean once = intent.getBooleanExtra("once", true);
        if (once) {
//            ClockCtrl.
            //TODO 使用id 来寻找 ClockItem
        }


        Toast.makeText(MainActivity.getContext(), "Time up !", Toast.LENGTH_LONG).show();
    }
}
