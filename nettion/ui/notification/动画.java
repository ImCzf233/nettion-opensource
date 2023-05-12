package nettion.ui.notification;

import nettion.utils.Direction;
import nettion.utils.time.TimerUtil;

/**
 * This animation superclass was made by Foggy and advanced by cedo
 *
 * @author Foggy
 * @author cedo
 * @since 7/21/2020 (yes 2020)
 * @since 7/29/2021
 */
public abstract class 动画 {

    public TimerUtil timerUtil = new TimerUtil();
    protected int duration;
    protected double endPoint;
    protected Direction direction;

    public 动画(int ms, double endPoint) {
        this(ms, endPoint, Direction.FORWARDS);
    }

    public 动画(int ms, double endPoint, Direction direction) {
        this.duration = ms; //Time in milliseconds of how long you want the animation to take.
        this.endPoint = endPoint; //The desired distance for the animated object to go.
        this.direction = direction; //Direction in which the graph is going. If backwards, will start from endPoint and go to 0.
    }


    public boolean finished(Direction direction) {
        return isDone() && this.direction.equals(direction);
    }

    public boolean isDone() {
        return timerUtil.hasTimeElapsed(duration);
    }

    public Direction getDirection() {
        return direction;
    }

    public 动画 setDirection(Direction direction) {
        if (this.direction != direction) {
            this.direction = direction;
            timerUtil.setTime(System.currentTimeMillis() - (duration - Math.min(duration, timerUtil.getTime())));
        }
        return this;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    protected boolean correctOutput() {
        return false;
    }

    public Double getOutput() {
        if (direction.forwards()) {
            if (isDone()) {
                return endPoint;
            }

            return getEquation(timerUtil.getTime() / (double) duration) * endPoint;
        } else {
            if (isDone()) {
                return 0.0;
            }

            if (correctOutput()) {
                double revTime = Math.min(duration, Math.max(0, duration - timerUtil.getTime()));
                return getEquation(revTime / (double) duration) * endPoint;
            }

            return (1 - getEquation(timerUtil.getTime() / (double) duration)) * endPoint;
        }
    }


    //This is where the animation equation should go, for example, a logistic function. Output should range from 0 - 1.
    //This will take the timer's time as an input, x.
    protected abstract double getEquation(double x);

}
