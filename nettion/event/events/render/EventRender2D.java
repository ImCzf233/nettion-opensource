/*
 * Decompiled with CFR 0_132.
 */
package nettion.event.events.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import nettion.event.Event;

public class EventRender2D
extends Event {
    private float partialTicks;
    public ScaledResolution sr;

    public ScaledResolution getSR() {
        return this.sr;
    }

    public EventRender2D(float partialTicks) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        this.partialTicks = partialTicks;
        this.sr = scaledResolution;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}

