package mine.android.HeavenClock;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.*;
import mine.android.controller.ClockCtrl;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Heaven on 2015/2/2.
 */
public class MainActivity extends Activity {
    private static Context context = null;
    private TextView debugView = null;
    private Handler handler = null;

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        context = this;
        debugView = (TextView) findViewById(R.id.debugView);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                debugView.setText(msg.obj.toString());
            }
        };

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    handler.sendMessage(Message.obtain(handler, 0, new Date().toString()));
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        Button addClockBtn = (Button) findViewById(R.id.addClockBtn);
        addClockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(
                        getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                if (!view.isShown())
                                    return;
                                Calendar time = Calendar.getInstance();
                                time.setTimeInMillis(System.currentTimeMillis());
                                time.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                time.set(Calendar.MINUTE, minute);
                                time.set(Calendar.SECOND, 0);
                                time.set(Calendar.MILLISECOND, 0);

                                Date date = time.getTime();
                                ClockCtrl.addClockItem(date);
                                renderClockListView();

                                String line = getString(R.string.set_clock_msg).replaceFirst("\\$\\{time\\}", date.getHours() + ":" + date.getMinutes());
                                Toast.makeText(getContext(), line, Toast.LENGTH_LONG).show();
                            }
                        },
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                        Calendar.getInstance().get(Calendar.MINUTE),
                        true
                ).show();
            }
        });

        ListView lv = (ListView) findViewById(R.id.listView);
        final BaseAdapter clockListAdapter = ClockCtrl.getClockListForListView();
        lv.setAdapter(clockListAdapter);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ClockCtrl.delClockItem(position);

                String line = getString(R.string.del_clock_msg);
                Toast.makeText(getContext(), line, Toast.LENGTH_LONG).show();

                renderClockListView();
                debugView.setText("You just long touch " + position + "th line");
                return true;
            }
        });

        //activate all clock
        ClockCtrl.activateAllClockItem();
    }

    private void renderClockListView() {
        ListView lv = (ListView) findViewById(R.id.listView);
        final BaseAdapter clockListAdapter = ClockCtrl.getClockListForListView();
        lv.setAdapter(clockListAdapter);
    }
}
