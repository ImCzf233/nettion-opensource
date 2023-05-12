package nettion.ui.clickgui.astolfo;

public class TimerUtils {
    private static long prevMS;
    private static long lastMS;
    private final long ms = getCurrentMS();

    public TimerUtils() {
        prevMS = 0L;
        lastMS = -1L;
    }

    public static boolean delay(final float milliSec) {
        return getTime() - prevMS >= milliSec;
    }

    public boolean delay(double nextDelay) {
        return (System.currentTimeMillis() - lastMS) >= nextDelay;
    }

    public static void reset() {
        prevMS = getTime();
    }

    public static long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public static long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public static boolean hasReached(final long milliseconds) {
        return getCurrentMS() - lastMS >= milliseconds;
    }

    public static boolean hasReached(final double milliseconds) {
        return getCurrentMS() - lastMS >= milliseconds;
    }

    public static boolean hasTimeElapsed(final long time, final boolean reset) {
        if (getTime() >= time) {
            if (reset) {
                reset();
            }
            return true;
        }
        return false;
    }

    public final boolean elapsed(long milliseconds) {
        return getCurrentMS() - ms > milliseconds;
    }

    public final boolean hasPassed(final long milliseconds) {
        return getCurrentMS() - lastMS > milliseconds;
    }
}
