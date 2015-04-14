package mine.android.controller;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import mine.android.api.WebAPI;
import mine.android.modules.ClockSong;

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

    private MediaPlayer mp = null;

    private ExecutorService threadPool = null;

    private Queue<ClockSong> playList = new LinkedList<ClockSong>();

    private int maxSize = 3;

    private int playedSong = 0;

    private ClockSong currentSong = null;

    private void playClockSong(ClockSong song) {
        try {
            currentSong = song;

            mp.reset();
            mp.setDataSource(song.getUrl());
            mp.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DouBanPlayer(int maxSize) {
        this.maxSize = maxSize;

        mp = new MediaPlayer();
        mp.setAudioStreamType(AudioManager.STREAM_ALARM);
        mp.setOnCompletionListener(this);
        mp.setOnPreparedListener(this);

        threadPool = Executors.newSingleThreadExecutor();
    }

    /**
     * 获取当前播放的音乐
     * @return
     */
    public ClockSong getCurrentSong() {
        return currentSong;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // 更新播放歌曲数
        playedSong++;
        markCurrentSong(WebAPI.OP_END);

        if (playedSong < maxSize)
            nextSong();
    }

    private void nextSong() {
        // 如果没有歌曲，则获取歌曲
        while (playList.peek() == null) {
            updatePlayList();
        }

        // 从播放队列中弹出歌曲并播放
        ClockSong song = playList.poll();
        playClockSong(song);
    }

    private void updatePlayList() {
        Random seed = new Random();
        List<ClockSong> gottenList = null;

        // 等概率的获取两个列表
        if (seed.nextBoolean())
            gottenList = WebAPI.SongListOperation(CHANNEL_NEW, WebAPI.OP_GET_NEW_LIST);
        else
            gottenList = WebAPI.SongListOperation(CHANNEL_OLD, WebAPI.OP_GET_NEW_LIST);

        // 将获得的播放列表加入播放队列中
        // 只加入一定数量的歌曲
        for (ClockSong item : gottenList) {
            playList.add(item);
            if (playList.size() == maxSize)
                break;
        }

    }

    public void markCurrentSong(final char op) {
        Thread markThread = new Thread() {
            @Override
            public void run() {
                WebAPI.SongListOperation(-1, op, currentSong.getSid());
            }
        };
        threadPool.execute(markThread);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Log.i("Douban", "onPrepared");
    }

    public int getPlayedSong() {
        return playedSong;
    }
}
