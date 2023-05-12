package nettion.utils.time;

public class TimerUtil {
    public long lastMS = System.currentTimeMillis();

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }

    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public void setTime(long time) {
        lastMS = time;
    }

    public static boolean hasTimePassed(long MS) {
        return System.currentTimeMillis() >= -1L + MS;
    }
}
