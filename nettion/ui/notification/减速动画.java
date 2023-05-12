package nettion.ui.notification;

public class 减速动画 extends 动画 {

    public 减速动画(int ms, double endPoint) {
        super(ms, endPoint);
    }

    protected double getEquation(double x) {
        return 1 - ((x - 1) * (x - 1));
    }
}
