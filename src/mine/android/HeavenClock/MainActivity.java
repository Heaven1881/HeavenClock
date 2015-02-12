package mine.android.HeavenClock;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import mine.android.controller.ClockCtrl;

import java.util.Calendar;

/**
 * Created by Heaven on 2015/2/2.
 */
public class MainActivity extends Activity {
    private static Context context = null;
    private TextView debugView = null;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        context = this;
        debugView = (TextView) findViewById(R.id.debugView);

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(ClockCtrl.getClockListAdapter());
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Log.i("ListView", "You just touch " + id + "th line");
                debugView.setText("You just touch " + id + "th line");
            }
        });

        Button addClockBtn = (Button) findViewById(R.id.addClockBtn);
        addClockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                Calendar time = Calendar.getInstance();
                                time.setTimeInMillis(System.currentTimeMillis());
                                time.set(Calendar.HOUR, hourOfDay);
                                time.set(Calendar.MINUTE, minute);
                                time.set(Calendar.SECOND, 0);
                                time.set(Calendar.MILLISECOND, 0);

                                ClockCtrl.addClockItem(time.getTime());

                                Toast.makeText(getContext(), "add clock: " + time.getTime().toLocaleString(), Toast.LENGTH_LONG).show();
                            }
                        },
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE),
                        true
                ).show();
            }
        });
    }
}
