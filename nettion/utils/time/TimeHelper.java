package nettion.utils.time;

public class TimeHelper {
    public static long lastMs;

    public TimeHelper() {
        lastMs = 0L;
    }

    public void reset() {
        lastMs = System.currentTimeMillis();
    }

    public static boolean isDelayComplete(double valueState) {
        return System.currentTimeMillis() - lastMs >= valueState;
    }

    public boolean delay(long nextDelay) {
        return System.currentTimeMillis() - lastMs >= nextDelay;
    }

    public boolean delay(float nextDelay) {
        return System.currentTimeMillis() - lastMs >= nextDelay;
    }
}

