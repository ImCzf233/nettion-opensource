package nettion.utils.time;

public class TimerUtils {
    public long lastMS;

    public boolean hasTimePassed(final long MS) {
        long time = -1L;
        return System.currentTimeMillis() >= time + MS;
    }

    private long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public boolean hasReached(double milliseconds) {
        return (double) (getCurrentMS() - this.lastMS) >= milliseconds;
    }

    public void setTime(long time) {
        lastMS = time;
    }

    public boolean hasPassed(double milli) {
        return System.currentTimeMillis() - this.lastMS >= milli;
    }

    public boolean isDelayComplete(long delay) {
        return System.currentTimeMillis() - this.lastMS > delay;
    }

    public void reset() {
        this.lastMS = getCurrentMS();
    }

    public boolean delay(float milliSec) {
        return (float) (getTime() - this.lastMS) >= milliSec;
    }

    public long getTime() {
        return System.nanoTime() / 1000000L;
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (lastMS > System.currentTimeMillis()) {
            lastMS = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - lastMS > time) {
            if (reset)
                reset();

            return true;
        } else {
            return false;
        }
    }
}