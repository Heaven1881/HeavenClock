package mine.android.api;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import mine.android.api.modules.Json;
import mine.android.api.modules.JsonArray;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Heaven on 2015/4/14.
 */
public class DouBanPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private static ExecutorService threadPool = null;

    private Queue<Json> playQueue = null;
    private OnNewSongListener onNewSongListener = null;
    private MediaPlayer mp = null;
    private Json currentSong = null;

    public DouBanPlayer() {
        // 初始化线程池
        if (threadPool == null)
            threadPool = Executors.newSingleThreadExecutor();
        // 初始化播放器
        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_ALARM);
        mp.setOnCompletionListener(this);
        mp.setOnPreparedListener(this);
        // 初始化播放队列
        playQueue = new LinkedList<>();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (this.onNewSongListener != null)
            this.onNewSongListener.onNewSong(currentSong);
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // mp status: playbackCompleted
        //检查播放列表里是否还有音乐，有则播放
        try {
            if (playQueue.size() > 0) {
                Json song = playQueue.poll();
                this.stopThenPlay(song);
            } else {
                Log.i("DouBanPlayer", "no more song in queue");
                mp.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    public void stopThenPlay(Json song) {
        // mp status: playbackCompleted | started |Initialed
        if (mp.isPlaying()) // Initialed 状态下不能调用stop
            mp.stop();
        this.resetAngPlay(song);
    }

    public void resetAngPlay(Json song) {
        // mp status: stop | idle
        currentSong = song;
        String songUrl = song.getString("url");

        try {
            mp.reset();
            mp.setDataSource(songUrl);
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnNewSongListener(OnNewSongListener listener) {
        this.onNewSongListener = listener;
    }

    public Json getCurrentSong() {
        return currentSong;
    }

    public void start(){
        // mp status: idle
        //检查播放列表里是否还有音乐，有则播放
        if (playQueue.size() > 0) {
            Json song = playQueue.poll();
            this.resetAngPlay(song);
        } else {
            Log.i("DouBanPlayer", "no more song in queue, mp cannot start");
        }
    }

    /**
     * 跳过当前歌曲，播放下一首
     */
    public void skipCurrentSong() {
        if (playQueue.size() > 0) {
            Json song = playQueue.poll();
            this.stopThenPlay(song);
        } else {
            Log.i("DouBanPlayer", "no more song in queue, mp cannot continue");
            mp.stop();
        }
    }

    public void stopAndRelease() {
        if (mp.isPlaying())
            mp.stop();
        mp.reset();
        mp.release();
    }

    public void addSong(JsonArray array) {
        for (Json json : array) {
            this.playQueue.offer(json);
        }
        Log.i("addSong", String.format("add %d song", array.length()));
    }

    public int sizeOfPlayQueue() {
        return this.playQueue.size();
    }

    public interface OnNewSongListener {
        public void onNewSong(Json song);
    }
}
