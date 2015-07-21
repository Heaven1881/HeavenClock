package mine.android.api.modules;

import java.io.Serializable;

/**
 * Created by Heaven on 2015/2/16.
 */
public class Config implements Serializable, Comparable<Config> {

    private String doubanEmail = "";
    private String doubanPassword = "";
    private int repeatSong = 1;
    private double pForNew = 0.5;


    @Override
    public int compareTo(Config another) {
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

    public double getpForNew() {
        return pForNew;
    }

    public void setpForNew(double pForNew) {
        this.pForNew = pForNew;
    }
}
