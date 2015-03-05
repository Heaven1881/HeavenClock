package mine.android.HeavenClock;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
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
                    Date date = new Date();
                    handler.sendMessage(Message.obtain(handler, 0, date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds()));
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

        Button settingBtn = (Button) findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SettingActivity.class);
                startActivity(intent);
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

                startActivityForResult(intent, ClockDetailActivity.MODE_UPDATE);
            }
        });

        //activate all clock
        ClockCtrl.activateAllClockItem();

        Toast.makeText(getContext(), getString(R.string.activate_clock), Toast.LENGTH_LONG).show();
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
        } else if (requestCode == ClockDetailActivity.MODE_UPDATE && resultCode == ClockDetailActivity.RESULT_UPDATE) {
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
//        renderClockListView();
    }

    public static void toastClockItem(ClockItem item) {
        String str = MainActivity.getContext().getString(R.string.clock_added);
        str = str.replaceFirst("\\{H\\}", item.getTime().getHours() + "");
        str = str.replaceFirst("\\{M\\}", item.getTime().getMinutes() + "");
        Toast.makeText(MainActivity.getContext(), str, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            PackageManager packageManager = getPackageManager();
            ResolveInfo homeInfo = packageManager.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);
            ActivityInfo ai = homeInfo.activityInfo;

            Intent startIntent = new Intent(Intent.ACTION_MAIN);
            startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startIntent.setComponent(new ComponentName(ai.packageName, ai.name));

            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        renderClockListView();
    }

    private void renderClockListView() {
        ListView lv = (ListView) findViewById(R.id.listView);
        final BaseAdapter clockListAdapter = ClockCtrl.getClockListForListView();
        lv.setAdapter(clockListAdapter);
    }
}
