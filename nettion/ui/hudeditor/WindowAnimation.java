package nettion.ui.hudeditor;

public class WindowAnimation {
    public static double delta;
    public static float moveUD(float current, float end, float smoothSpeed, float minSpeed) {
        float movement = (end - current) * smoothSpeed;
        if (movement > 10.0f) {
            movement = Math.max((float) minSpeed, (float) movement);
            movement = Math.min((float) (end - current), (float) movement);
        } else if (movement < 10.0f) {
            movement = Math.min((float) (-minSpeed), (float) movement);
            movement = Math.max((float) (end - current), (float) movement);
        }
        return current + movement;
    }
}
