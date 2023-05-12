package nettion.event.events.misc;

import nettion.event.Event;

public class EventJump extends Event {
    public EventJump(float jumpMotion, float yaw) {
        this.jumpMotion = jumpMotion;
        this.yaw = yaw;
    }

    private float jumpMotion;
    private float yaw;

    public float getJumpMotion() {
        return jumpMotion;
    }

    public void setJumpMotion(float jumpMotion) {
        this.jumpMotion = jumpMotion;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
