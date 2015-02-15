package mine.android.modules;

/**
 * Created by Heaven on 2015/2/15.
 */
public class ClockSong {
    private String title = null;
    private String url = null;
    private String artist = null;

    public ClockSong(String url) {
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
}
