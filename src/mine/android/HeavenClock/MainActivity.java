package mine.android.HeavenClock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.*;
import mine.android.controller.ClockCtrl;
import mine.android.modules.ClockItem;

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
    protected void onCreate(final Bundle savedInstanceState) {
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
                Intent intent = new Intent(getContext(), ClockDetailActivity.class);
                intent.putExtra("mode", ClockDetailActivity.MODE_ADD);
                startActivityForResult(intent, ClockDetailActivity.MODE_ADD);
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
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ClockDetailActivity.class);
                intent.putExtra("mode", ClockDetailActivity.MODE_UPDATE);
                intent.putExtra("position", position);

                startActivityForResult(intent, ClockDetailActivity.MODE_ADD);
            }
        });

        //activate all clock
        ClockCtrl.activateAllClockItem();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ClockDetailActivity.MODE_ADD && resultCode == ClockDetailActivity.RESULT_ADD) {
            int r = data.getIntExtra("repeat", ClockItem.NO_REPEAT);
            Date t = new Date(data.getLongExtra("date", 0));
            String d = data.getStringExtra("description");

            ClockItem item = new ClockItem(t, d, r);
            ClockCtrl.addClockItem(item);
            renderClockListView();
        } else if (requestCode == ClockDetailActivity.MODE_UPDATE && requestCode == ClockDetailActivity.RESULT_UPDATE) {
            int r = data.getIntExtra("repeat", ClockItem.NO_REPEAT);
            Date t = new Date(data.getLongExtra("date", 0));
            String d = data.getStringExtra("description");
            int position = data.getIntExtra("position", -1);

            if (position == -1) {
                Log.i("clock return", "position == -1");
            }

            ClockItem item = new ClockItem(t, d, r);
            ClockCtrl.replaceClockItem(position, item);
        }
    }

    private void renderClockListView() {
        ListView lv = (ListView) findViewById(R.id.listView);
        final BaseAdapter clockListAdapter = ClockCtrl.getClockListForListView();
        lv.setAdapter(clockListAdapter);
    }
}
