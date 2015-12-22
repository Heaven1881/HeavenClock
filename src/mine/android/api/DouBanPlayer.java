package mine.android.api;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import mine.android.api.modules.Config;
import mine.android.api.modules.Song;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Heaven on 2015/4/14.
 */
public class DouBanPlayer implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    private static int CHANNEL_NEW = 0;
    private static int CHANNEL_OLD = -3;
    private double p = 0.3;

//    private static DouBanPlayer player = null;

    private MediaPlayer mp = null;

    private ExecutorService threadPool = null;

    private Queue<Song> playList = new LinkedList<Song>();

    private int size = 3;

    private int playedSong = 0;

    private Song currentSong = null;

    private OnNewSongListener onNewSongListener = null;

    public static DouBanPlayer newInstance() {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        Config config = ConfigAPI.get();
        return new DouBanPlayer(config.getRepeatSong(), config.getpForNew(), pool);
    }

    private void playSong(Song song) {
        try {
            currentSong = song;

            // 调用onNewSongListener做进一步处理
            if (onNewSongListener != null)
                onNewSongListener.onNewSong(song);

            playedSong++;

            mp.reset();
            Log.d("mp-status", "reset");
            mp.setDataSource(song.getUrl());
            Log.d("mp-status", "setDataSource");
            mp.prepareAsync();
            Log.d("mp-status", "prepareAsync");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnNewSongListener(OnNewSongListener listener) {
        this.onNewSongListener = listener;
    }

    private DouBanPlayer(int size, double p, ExecutorService threadPool) {
        this.size = size;
        this.p = p;

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_ALARM);
        mp.setOnCompletionListener(this);
        mp.setOnPreparedListener(this);

        this.threadPool = threadPool;
    }

    /**
     * 获取当前播放的音乐
     *
     * @return
     */
    public Song getCurrentSong() {
        return currentSong;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // 更新播放歌曲数
        markSong(DoubanAPI.OP_END, currentSong.getSid());

        if (playedSong >= size) {
            stop();
            return;
        }

        nextSong();
    }

    public void start() {
        DoubanAPI.getLoginInfo(true);

        int maxTry = 10;
        while (playList.size() < size && --maxTry > 0) {
            updatePlayList();
        }
        nextSong();
    }

    private void nextSong() {
        // 如果没有歌曲，则停止
        if (playList.peek() == null) {
            this.stop();
            return;
        }

        // 从播放队列中弹出歌曲并播放
        Song song = playList.poll();
        playSong(song);
    }

    private void updatePlayList() {
        Random seed = new Random();
        List<Song> gottenList = null;

        // 选取歌的概率
        double v = seed.nextDouble();
//        Log.i("p=" + p, "v=" + v);
        if (v < p)
            gottenList = DoubanAPI.getNewList(CHANNEL_NEW);
        else
            gottenList = DoubanAPI.getNewList(CHANNEL_OLD);

        // 将获得的播放列表加入播放队列中
        // 只加入一定数量的歌曲
        for (Song item : gottenList) {
            playList.add(item);
        }

    }

    /**
     * 跳过当前歌曲，播放下一首
     */
    public void skipCurrentSong() {
        mp.reset();
        Log.d("mp-status", "reset");
        markSong(DoubanAPI.OP_SKIP, currentSong.getSid());
        nextSong();
    }

    public void markCurrentSong(char op) {
        markSong(op, currentSong.getSid());
    }

    private void markSong(final char op, final int sid) {
        Thread markThread = new Thread() {
            @Override
            public void run() {
                DoubanAPI.remoteOperation(-1, op, sid);
            }
        };
        threadPool.execute(markThread);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Log.d("mp-status", "start");
        Log.i("Douban", "onPrepared");
    }

    public int getPlayedSong() {
        return playedSong;
    }

    public void stop() {
        if (mp.isPlaying()) {
            mp.stop();
            Log.d("mp-status", "stop");
        }
        mp.release();
        Log.d("mp-status", "release");

    }

    public interface OnNewSongListener {
        public void onNewSong(Song song);
    }
}
