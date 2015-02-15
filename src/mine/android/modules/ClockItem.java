package mine.android.modules;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Heaven on 2015/2/12.
 */
public class ClockItem implements Serializable, Comparable<ClockItem> {
    public static final int FIELD_TIME = 10;
    public static final int FIELD_DESCRIPTION = 11;
    public static final int FIELD_REPEAT = 12;
    public static final int FIELD_ACTIVATED = 13;

    public static final int NO_REPEAT = 0;
    public static final int EVERY_DAY = 1;
    public static final int EVERY_WEEK = 2;

    private Date time = null;
    private String description = null;
    private int repeat = NO_REPEAT;
    private boolean activated = true;

    public ClockItem(Date time, String description, int repeat) {
        this.time = time;
        this.description = description;
        this.repeat = repeat;
    }

    public int getCompareId() {
        return repeat +
                100 * time.getMinutes() +
                10000 * time.getHours();
    }

    public ClockItem(Date time, int repeat) {
        this.time = time;
        this.repeat = repeat;
    }

    @Override
    public int compareTo(ClockItem another) {
        if (this.time.equals(another.time)
                && this.repeat == another.repeat)
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

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
