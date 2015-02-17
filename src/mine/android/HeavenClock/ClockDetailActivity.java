package mine.android.HeavenClock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import mine.android.controller.ClockCtrl;
import mine.android.modules.ClockItem;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Heaven on 2015/2/16.
 */
public class ClockDetailActivity extends Activity {
    public static int MODE_ADD = 1;
    public static int MODE_UPDATE = 2;

    public static int RESULT_NOTHING_CHANGE = 10;
    public static int RESULT_ADD = 11;
    public static int RESULT_UPDATE = 12;

    TimePicker timePicker = null;
    EditText description = null;

    RadioButton noRepeatRb = null;
    RadioButton everyDayRb = null;

    private int pos = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_detail);

        final Intent intent = getIntent();
        final int mode = intent.getIntExtra("mode", 0);

        String descriptionInfo = getString(R.string.no_description);
        int repeatInfo = ClockItem.NO_REPEAT;
        Date timeInfo = new Date();
        if (mode == MODE_UPDATE) {
            int position = intent.getIntExtra("position", -1);
            pos = position;
            ClockItem item = ClockCtrl.getClockItemByPos(position);
            descriptionInfo = item.getDescription();
            repeatInfo = item.getRepeat();
            timeInfo = item.getTime();
        }

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);
        timePicker.setCurrentHour(timeInfo.getHours());
        timePicker.setCurrentMinute(timeInfo.getMinutes());

        description = (EditText) findViewById(R.id.description);
        description.setText(descriptionInfo);

        noRepeatRb = (RadioButton) findViewById(R.id.no_repeat_rb);
        everyDayRb = (RadioButton) findViewById(R.id.every_day_rb);
        if (repeatInfo == ClockItem.NO_REPEAT) {
            noRepeatRb.setChecked(true);
        } else {
            everyDayRb.setChecked(true);
        }

        Button okBtn = (Button) findViewById(R.id.ok_button);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dcpRet = String.valueOf(description.getText());
                int repRet = ClockItem.NO_REPEAT;
                if (everyDayRb.isChecked())
                    repRet = ClockItem.EVERY_DAY;
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                c.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                long timeInMillis = c.getTimeInMillis();

                Intent it = new Intent();
                if (mode == MODE_ADD) {
                    it.putExtra("repeat", repRet);
                    it.putExtra("date", timeInMillis);
                    it.putExtra("description", dcpRet);
                    setResult(RESULT_ADD, it);
                } else if (mode == MODE_UPDATE) {
                    it.putExtra("repeat", repRet);
                    it.putExtra("date", timeInMillis);
                    it.putExtra("description", dcpRet);
                    it.putExtra("position", pos);
                    setResult(RESULT_UPDATE, it);
                }

                finish();
            }
        });

        Button cancelBtn = (Button) findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_NOTHING_CHANGE);
                finish();
            }
        });
    }
}