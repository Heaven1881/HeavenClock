package mine.android.api.modules;

import android.provider.ContactsContract;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Heaven on 2015/2/15.
 */
public class Song implements Serializable, Comparable<Song> {
    private String title = null;
    private String url = null;
    private String artist = null;
    private int sid = 0;
    private Date playTime = new Date();
    private boolean like = false;

    public Song(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getSid() {
        return sid;
    }

    public void setSid(int sid) {
        this.sid = sid;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    @Override
    public int compareTo(Song another) {
        return (int) (another.getPlayTime().getTime() - this.playTime.getTime());
    }

    public Date getPlayTime() {
        return playTime;
    }

    public void setPlayTime(Date playTime) {
        this.playTime = playTime;
    }
}
