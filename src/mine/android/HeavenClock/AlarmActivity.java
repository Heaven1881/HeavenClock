package mine.android.HeavenClock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import mine.android.api.WebAPI;
import mine.android.controller.ClockCtrl;
import mine.android.modules.ClockSong;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by Heaven on 2015/2/12.
 */
public class AlarmActivity extends Activity implements Runnable,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener {
    private MediaPlayer mp = null;
    private TextView showView = null;
    private Handler uiHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);

        showView = (TextView) findViewById(R.id.clock_msg);
        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                showView.setText((String) msg.obj);
            }
        };

        Intent intent = getIntent();
        boolean once = intent.getBooleanExtra("once", true);
        if (once) {
            int compareId = intent.getIntExtra("compareId", 0);
            ClockCtrl.setClockItemDisableByCompareId(compareId);
        }

        Toast.makeText(MainActivity.getContext(), "Time up !", Toast.LENGTH_LONG).show();
    }

    @Override
    public void run() {
        List<ClockSong> songList = WebAPI.getSongList(0, 'n');

        if (songList.size() < 1)
            return;
        ClockSong song = songList.get(0);

        try {
            mp.reset();
            mp.setDataSource(song.getUrl());
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static File downloadFile(String u, String filename) throws IOException {
        Log.i("download url", u);
        URL url = new URL(u);
        URLConnection uc = url.openConnection();
        InputStream is = uc.getInputStream();
        byte[] buffer = new byte[1024 * 4];
        File filePath = new File(MainActivity.getContext().getCacheDir(), filename);
        Log.i("download file name", filePath.getAbsolutePath());
        FileOutputStream os = MainActivity.getContext().openFileOutput(filePath.getName(), Context.MODE_WORLD_WRITEABLE);
        int length = uc.getContentLength();
        long all = 0;
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
            all += len;
        }

        Log.i("file download", "competed");
        os.close();
        is.close();

        return filePath;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }
}
