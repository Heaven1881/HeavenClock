package mine.android.HeavenClock;

import android.app.Activity;
import android.content.Intent;
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
import mine.android.controller.DouBanPlayer;
import mine.android.modules.ClockSong;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Heaven on 2015/2/12.
 */
public class AlarmActivity extends Activity implements Runnable {
    private static final int SHOW_VIEW = 1;
    private static final int TITLE = 2;
    private static final int ARTIST = 3;
    private static final int TOAST = 4;
    private static final int LIKE_BUTTON = 5;

    //媒体播放任务
    private DouBanPlayer player = null;
    private int repeatTime = 1;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm);

        //初始化线程池
        ExecutorService pool = Executors.newSingleThreadExecutor();

        //初始化播放器
        repeatTime = ConfigAPI.getConfig().getRepeatSong();
        player = new DouBanPlayer(repeatTime, pool);


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
                player.stop();
                AlarmActivity.this.finish();
            }
        });

        // 标记歌曲为喜欢按钮
        likeButton = (Button) findViewById(R.id.likeBtn);
        mark = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.markCurrentSong(WebAPI.OP_MARK_AS_LIKE);

                Toast.makeText(AlarmActivity.this, AlarmActivity.this.getString(R.string.mark_as_like), Toast.LENGTH_LONG).show();
                likeButton.setText(getString(R.string.already_like));
                likeButton.setOnClickListener(dmark);
            }
        };
        dmark = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.markCurrentSong(WebAPI.OP_DMARK_LIKE);

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
                player.markCurrentSong(WebAPI.OP_BYE);
                Toast.makeText(AlarmActivity.this, AlarmActivity.this.getString(R.string.mark_as_unlike), Toast.LENGTH_LONG).show();
                tryPlayNextSong();
            }
        });

        // 下一首
        Button next = (Button) findViewById(R.id.nextBtn);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryPlayNextSong();
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

    private void tryPlayNextSong() {
        Log.i("playsong:", player.getPlayedSong() + "");
        if (player.getPlayedSong() >= repeatTime) {
            String line = MainActivity.getContext().getString(R.string.no_more_song);
            line = line.replaceFirst("\\{n\\}", String.valueOf(repeatTime));
            Toast.makeText(MainActivity.getContext(), line, Toast.LENGTH_LONG).show();
            return;
        }
        player.skipCurrentSong();
    }

    @Override
    public void run() {
        player.setOnNewSongListener(new DouBanPlayer.OnNewSongListener() {
            @Override
            public void onNewSong(ClockSong song) {
                //更新UI界面歌曲信息
                uiHandler.sendMessage(Message.obtain(uiHandler, TITLE, song.getTitle()));
                uiHandler.sendMessage(Message.obtain(uiHandler, ARTIST, song.getArtist()));
                uiHandler.sendMessage(Message.obtain(uiHandler, LIKE_BUTTON, song.isLike()));

                Log.i("mp3 url   :", song.getUrl());
                Log.i("mp3 title :", song.getTitle());
                Log.i("mp3 artist:", song.getArtist());
                Log.i("mp3 sid   :", String.valueOf(song.getSid()));
                Log.i("mp3 like  :", String.valueOf(song.isLike()));
            }
        });
        player.start();
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
