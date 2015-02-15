package mine.android.HeavenClock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import mine.android.controller.ClockCtrl;

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
            int compareId = intent.getIntExtra("compareId", 0);
            ClockCtrl.setClockItemDisableByCompareId(compareId);
            //TODO 判断时间，若在该时间之前，则不需要激活
            //TODO 使用id 来寻找 ClockItem
            //TODO 重构代码结构
        }


        Toast.makeText(MainActivity.getContext(), "Time up !", Toast.LENGTH_LONG).show();
    }
}
