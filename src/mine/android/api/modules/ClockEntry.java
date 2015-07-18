package mine.android.api.modules;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Heaven on 15/7/18
 */
public class ClockEntry implements Serializable, Comparable<ClockEntry> {
    public ClockType getType() {
        return type;
    }

    public void setType(ClockType type) {
        this.type = type;
    }

    enum ClockType {FOR_ONCE, FOR_DAY, FOR_WEEK}

    private static int nextId = -1;

    public static int getNextId() {
        return ++nextId;
    }

    private int id = 0;
    private String name = null;
    private Date time = new Date(0);
    private boolean active = true;
    private ClockType type = ClockType.FOR_ONCE;
    private short weeks = 0x7f;

    @Override
    public int compareTo(ClockEntry another) {
        return this.id - another.id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
