package mine.android.modules;

import java.io.Serializable;

/**
 * Created by Heaven on 2015/2/16.
 */
public class Configuration implements Serializable, Comparable<Configuration> {

    private String doubanEmail = null;
    private String doubanPassword = null;
    private int repeatSong = 1;


    @Override
    public int compareTo(Configuration another) {
        return 0;
    }

    public String getDoubanPassword() {
        return doubanPassword;
    }

    public void setDoubanPassword(String doubanPassword) {
        this.doubanPassword = doubanPassword;
    }

    public String getDoubanEmail() {
        return doubanEmail;
    }

    public void setDoubanEmail(String doubanEmail) {
        this.doubanEmail = doubanEmail;
    }

    public int getRepeatSong() {
        return repeatSong;
    }

    public void setRepeatSong(int repeatSong) {
        this.repeatSong = repeatSong;
    }
}
