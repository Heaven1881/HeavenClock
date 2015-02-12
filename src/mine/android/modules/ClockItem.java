package mine.android.modules;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Heaven on 2015/2/12.
 */
public class ClockItem implements Serializable, Comparable<ClockItem> {
    public static final int NO_REPEAT = 0;
    public static final int EVERY_DAY = 1;
    public static final int EVERY_WEEK = 2;

    private Date time = null;
    private String description = null;
    private int repeat = NO_REPEAT;

    @Override
    public int compareTo(ClockItem another) {
        if (this.time.equals(another.time)
                && this.description.equals(another.description)
                && this.repeat == another.repeat )
            return 0;
        return this.time.compareTo(another.time);
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
