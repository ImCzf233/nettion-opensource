package nettion.ui.notification;

public class ���ٶ��� extends ���� {

    public ���ٶ���(int ms, double endPoint) {
        super(ms, endPoint);
    }

    protected double getEquation(double x) {
        return 1 - ((x - 1) * (x - 1));
    }
}
