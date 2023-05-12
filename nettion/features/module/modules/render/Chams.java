/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.lwjgl.opengl.GL11
 */
package nettion.features.module.modules.render;

import nettion.event.EventHandler;
import nettion.event.events.render.EventPostRenderPlayer;
import nettion.event.events.render.EventPreRenderPlayer;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import org.lwjgl.opengl.GL11;

public class Chams
extends Module {
    public Chams() {
        super("Chams", ModuleType.Render);
    }

    @EventHandler
    private void preRenderPlayer(EventPreRenderPlayer e) {
        GL11.glEnable(32823);
        GL11.glPolygonOffset(1.0f, -1100000.0f);
    }

    @EventHandler
    private void postRenderPlayer(EventPostRenderPlayer e) {
        GL11.glDisable(32823);
        GL11.glPolygonOffset(1.0f, 1100000.0f);
    }
}

