package mine.android.HeavenClock;

import android.app.Activity;
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

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Heaven on 2015/2/12.
 */
public class AlarmActivity extends Activity implements Runnable,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    private static final int SHOW_VIEW = 1;
    private static final int TITLE = 2;
    private static final int ARTIST = 3;
    private static final int TOAST = 4;
    private static final int LIKE_BUTTON = 5;

    //媒体播放任务
    private MediaPlayer mp = null;
    List<ClockSong> songList;

    //界面UI
    private TextView showView = null;
    private Handler uiHandler = null;
    private TextView artist = null;
    private TextView title = null;

    // 标记喜欢按钮
    private Button likeButton;
    private View.OnClickListener mark = null;
    private View.OnClickListener dmark = null;

    //界面显示的歌曲相关信息
    private int channel_id = -3;
    private int song_id = 0;

    //线程管理
    private ExecutorService pool = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        //初始化线程池
        pool = Executors.newSingleThreadExecutor();

        //初始化播放器
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.setOnPreparedListener(this);
        mp.setOnCompletionListener(this);
        mp.setAudioStreamType(AudioManager.STREAM_ALARM);

        //初始化UI组件
        artist = (TextView) findViewById(R.id.songArtist);
        title = (TextView) findViewById(R.id.songTitle);

        showView = (TextView) findViewById(R.id.clock_msg);
        uiHandler = new AlarmUIHandler();

        // 停止播放按钮
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

        // 标记歌曲为喜欢按钮
        likeButton = (Button) findViewById(R.id.likeBtn);
        mark = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread markLikeThread = new Thread() {
                    @Override
                    public void run() {
                        WebAPI.SongListOperation(channel_id, WebAPI.OP_MARK_AS_LIKE, song_id);
                        Log.i("mark as like", "sid = " + song_id);
                    }
                };
                pool.execute(markLikeThread);

                Toast.makeText(AlarmActivity.this, AlarmActivity.this.getString(R.string.mark_as_like), Toast.LENGTH_LONG).show();

                likeButton.setText(getString(R.string.already_like));
                likeButton.setOnClickListener(dmark);
            }
        };
        dmark = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread dmarkLikeThread = new Thread() {
                    @Override
                    public void run() {
                        WebAPI.SongListOperation(channel_id, WebAPI.OP_DMARK_LIKE, song_id);
                        Log.i("dmark for like", "sid = " + song_id);
                    }
                };
                pool.execute(dmarkLikeThread);

                Toast.makeText(AlarmActivity.this, AlarmActivity.this.getString(R.string.dmark_as_like), Toast.LENGTH_SHORT).show();
                likeButton.setText(getString(R.string.like));
                likeButton.setOnClickListener(mark);
            }
        };
        likeButton.setOnClickListener(mark);

        // 标记歌曲为不喜欢
        Button unlike = (Button) findViewById(R.id.dislikeBtn);
        unlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread markUnlikeThread = new Thread() {
                    @Override
                    public void run() {
                        WebAPI.SongListOperation(channel_id, WebAPI.OP_BYE, song_id);
                        Log.i("mark as unlike", "sid = " + song_id);
                    }
                };
                pool.execute(markUnlikeThread);

                Toast.makeText(AlarmActivity.this, AlarmActivity.this.getString(R.string.mark_as_unlike), Toast.LENGTH_LONG).show();
            }
        });

        // 下一首
        Button next = (Button) findViewById(R.id.nextBtn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mp.isPlaying())
                    return;
                if (songList.size() < 1) {
                    String line = MainActivity.getContext().getString(R.string.no_more_song);
                    line = line.replaceFirst("\\{n\\}", String.valueOf(ConfigAPI.getConfig().getRepeatSong()));
                    Toast.makeText(MainActivity.getContext(), line, Toast.LENGTH_LONG).show();
                    return;
                }
                mp.stop();
                final ClockSong song = songList.get(0);
                songList.remove(0);

                Thread playThread = new Thread() {
                    @Override
                    public void run() {
                        WebAPI.SongListOperation(channel_id, WebAPI.OP_SKIP, song_id);
                        playSong(song);
                    }
                };
                pool.execute(playThread);
            }
        });

        //检查闹钟信息
        Intent intent = getIntent();
        boolean once = intent.getBooleanExtra("once", true);
        if (once) {
            int compareId = intent.getIntExtra("compareId", 0);
            ClockCtrl.setClockItemDisableByCompareId(compareId);
        }

        //运行
        pool.execute(new Thread(this));
    }

    @Override
    public void run() {
        //获取歌曲列表
        songList = WebAPI.SongListOperation(channel_id, WebAPI.OP_GET_NEW_LIST);
        Log.i("get song list c = " + channel_id, "size = " + songList.size());
        if (songList.size() < 1) {
            String string = getString(R.string.log_err);
            uiHandler.sendMessage(Message.obtain(uiHandler, AlarmActivity.TOAST, string));
            uiHandler.sendMessage(Message.obtain(uiHandler, AlarmActivity.SHOW_VIEW, string));
            return;
        }

        for (ClockSong song : songList) {
            Log.i("songlist", song.getTitle());
        }

        //检查播放设置并调节歌曲列表长度
        Configuration config = ConfigAPI.getConfig();
        while (songList.size() < config.getRepeatSong()) {
            List<ClockSong> additionalSongList = WebAPI.SongListOperation(channel_id, WebAPI.OP_GET_NEXT_SONG);
            songList.addAll(additionalSongList);
        }
        while (songList.size() > config.getRepeatSong()) {
            songList.remove(songList.size() - 1);
        }

        //弹出并播放
        ClockSong song = songList.get(0);
        songList.remove(0);
        playSong(song);
    }

    private void playSong(ClockSong song) {
        //更新UI界面歌曲信息
        uiHandler.sendMessage(Message.obtain(uiHandler, TITLE, song.getTitle()));
        uiHandler.sendMessage(Message.obtain(uiHandler, ARTIST, song.getArtist()));
        uiHandler.sendMessage(Message.obtain(uiHandler, LIKE_BUTTON, song.isLike()));

        //播放
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

//    public static File downloadFile(String u, String filename) throws IOException {
//        Log.d("download url", u);
//        URL url = new URL(u);
//        URLConnection uc = url.openConnection();
//        InputStream is = uc.getInputStream();
//        byte[] buffer = new byte[1024 * 4];
//        File filePath = new File(MainActivity.getContext().getCacheDir(), filename);
//        Log.i("download file name", filePath.getAbsolutePath());
//        FileOutputStream os = MainActivity.getContext().openFileOutput(filePath.getName(), Context.MODE_WORLD_WRITEABLE);
//        int length = uc.getContentLength();
//        long all = 0;
//        int len;
//        while ((len = is.read(buffer)) != -1) {
//            os.write(buffer, 0, len);
//            all += len;
//        }
//
//        Log.i("file download", "competed");
//        os.close();
//        is.close();
//
//        return filePath;
//    }

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
        Thread completionThread = new Thread() {
            @Override
            public void run() {
                WebAPI.SongListOperation(channel_id, WebAPI.OP_END, song_id);
            }
        };
        pool.execute(completionThread);

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

    private class AlarmUIHandler extends Handler {
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
                case LIKE_BUTTON:
                    boolean like = (Boolean) msg.obj;
                    if (like)
                        likeButton.setText(getString(R.string.already_like));
                    else
                        likeButton.setText(getString(R.string.like));
                    break;
                default:
                    assert false;
            }
        }
    }
}
