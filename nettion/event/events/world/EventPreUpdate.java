/*
 * Decompiled with CFR 0_132.
 */
package nettion.event.events.world;

import nettion.event.Event;
import net.minecraft.network.Packet;

public class EventPreUpdate extends Event {
    private float yaw;
    private float pitch;
    public double y;
    private boolean ground;
    public Packet<?> packet;

    public Packet<?> getPacket() {
        return packet;
    }

    public EventPreUpdate(float yaw, float pitch, double y, boolean ground) {
        this.yaw = yaw;
        this.pitch = pitch;
        this.y = y;
        this.ground = ground;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setRotation(float[] rotation) {
        setYaw(rotation[0]);
        setPitch(rotation[1]);
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isOnGround() {
        return this.ground;
    }

    public void setGround(boolean ground) {
        this.ground = ground;
    }
}

