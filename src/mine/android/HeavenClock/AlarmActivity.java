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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import mine.android.api.ConfigAPI;
import mine.android.api.WebAPI;
import mine.android.controller.ClockCtrl;
import mine.android.modules.ClockSong;
import mine.android.modules.Configuration;

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
        MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    private static final int SHOW_VIEW = 1;
    private static final int TITLE = 2;
    private static final int ARTIST = 3;
    private static final int TOAST = 4;

    private MediaPlayer mp = null;
    List<ClockSong> songList;

    private TextView showView = null;
    private Handler uiHandler = null;
    private TextView artist = null;
    private TextView title = null;

    private int cancel_id = 0;
    private int song_id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setVolume(1.0f, 1.0f);

        artist = (TextView) findViewById(R.id.songArtist);
        title = (TextView) findViewById(R.id.songTitle);
//        Random random = new Random(System.currentTimeMillis());
//        cancel_id = random.nextInt() & 5;

        showView = (TextView) findViewById(R.id.clock_msg);
        uiHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SHOW_VIEW:
                        showView.setText((String) msg.obj);
                        break;
                    case TITLE:
                        title.setText((String) msg.obj);
                        break;
                    case ARTIST:
                        artist.setText((String) msg.obj);
                        break;
                    case TOAST:
                        Toast.makeText(MainActivity.getContext(), (String) msg.obj, Toast.LENGTH_LONG).show();
                        break;
                    default:
                        assert false;
                }
            }
        };

        Intent intent = getIntent();
        boolean once = intent.getBooleanExtra("once", true);
        if (once) {
            int compareId = intent.getIntExtra("compareId", 0);
            ClockCtrl.setClockItemDisableByCompareId(compareId);
        }

        Button stopBtu = (Button) findViewById(R.id.stopBtn);
        stopBtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mp.stop();
                    mp.release();
                } catch (Exception ignored) {
                }
                AlarmActivity.this.finish();
            }
        });

        Button likeButton = (Button) findViewById(R.id.likeBtn);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        WebAPI.SongListOperation(cancel_id, WebAPI.OP_MARK_AS_LIKE, song_id);
                        Log.i("mark as like", "sid = " + song_id);
                    }
                }.start();
                Toast.makeText(AlarmActivity.this, AlarmActivity.this.getString(R.string.mark_as_like), Toast.LENGTH_LONG).show();
            }
        });

        Button unlike = (Button) findViewById(R.id.dislikeBtn);
        unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        WebAPI.SongListOperation(cancel_id, WebAPI.OP_BYE, song_id);
                        Log.i("mark as unlike", "sid = " + song_id);
                    }
                }.start();
                Toast.makeText(AlarmActivity.this, AlarmActivity.this.getString(R.string.mark_as_unlike), Toast.LENGTH_LONG).show();
            }
        });

        new Thread(this).start();
    }

    @Override
    public void run() {
        songList = WebAPI.SongListOperation(cancel_id, WebAPI.OP_GET_NEXT_SONG);

        Log.i("get song list c = " + cancel_id, "size = " + songList.size());
        if (songList.size() < 1) {
            String string = getString(R.string.log_err);
            uiHandler.sendMessage(Message.obtain(uiHandler, AlarmActivity.TOAST, string));
            uiHandler.sendMessage(Message.obtain(uiHandler, AlarmActivity.SHOW_VIEW, string));
            return;
        }

        Configuration config = ConfigAPI.getConfig();
        while (songList.size() < config.getRepeatSong()) {
            List<ClockSong> additionalSongList = WebAPI.SongListOperation(cancel_id, WebAPI.OP_GET_NEW_LIST);
            songList.addAll(additionalSongList);
        }
        while (songList.size() > config.getRepeatSong()) {
            songList.remove(songList.size() - 1);
        }

        ClockSong song = songList.get(0);
        songList.remove(0);

        playSong(song);
    }

    private void playSong(ClockSong song) {
        uiHandler.sendMessage(Message.obtain(uiHandler, TITLE, song.getTitle()));
        uiHandler.sendMessage(Message.obtain(uiHandler, ARTIST, song.getArtist()));

        try {
            Log.i("mp3 url   :", song.getUrl());
            Log.d("mp3 title :", song.getTitle());
            Log.d("mp3 artist:", song.getArtist());
            Log.i("mp3 sid   :", String.valueOf(song.getSid()));

            song_id = song.getSid();

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
        Log.i("mp3", "onPrepared");
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        uiHandler.sendMessage(Message.obtain(uiHandler, SHOW_VIEW, "download..." + percent + "%"));
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (songList.size() < 1)
            return;

        ClockSong song = songList.get(0);
        songList.remove(0);

        Log.i("alarm", songList.size() + " songs left");

        playSong(song);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
