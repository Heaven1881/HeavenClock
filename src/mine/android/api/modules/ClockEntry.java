package mine.android.api.modules;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Heaven on 15/7/18
 */
@SuppressWarnings("ALL")
public class ClockEntry implements Serializable, Comparable<ClockEntry> {
    public ClockType getType() {
        return type;
    }

    public void setType(ClockType type) {
        this.type = type;
    }

    public int getHourOfDay() {
        return hourOfDay;
    }

    public void setHourOfDay(int hourOfDay) {
        this.hourOfDay = hourOfDay;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean weeks(int i) {
        return "1".equals(weeks.charAt(i));
    }

    public String getWeeks() {
        return weeks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public enum ClockType {FOR_ONCE, FOR_DAY, FOR_WEEK}

    private static int nextId = -1;

    public static int getNextId() {
        return ++nextId;
    }

    private int id = 0;
    private String name = "";
    private int hourOfDay = 0;
    private int minute = 0;
    private boolean active = true;
    private ClockType type = ClockType.FOR_ONCE;
    private String weeks = "1111111";

    public void setWeeks(String weeks) {
        this.weeks = weeks;
    }

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
