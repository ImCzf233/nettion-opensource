package nettion.features.module.modules.render;

import nettion.features.value.values.Mode;
import nettion.event.EventHandler;
import nettion.event.events.render.EventRender3D;
import nettion.event.events.world.EventPacketReceive;
import nettion.event.events.world.EventPostUpdate;
import nettion.event.events.world.EventPreUpdate;
import nettion.event.events.world.EventTick;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class WorldTime extends Module {
    public static Mode<Enum> mode = new Mode("mode", TimeMode.values(), TimeMode.Darkness);

    public WorldTime() {
        super("WorldTime", ModuleType.Render);
        addValues(mode);
    }

    @EventHandler
    private void onPreUpdate(EventPreUpdate e) {
        this.setSuffix(mode.getValue());
    }

    @EventHandler
    public void onRenderWorld(EventRender3D event) {
        if (mode.getValue() == TimeMode.Darkness) {
            mc.theWorld.setWorldTime(-18000);
        } else if (mode.getValue() == TimeMode.Sunset) {
            mc.theWorld.setWorldTime(-13000);
        } else if (mode.getValue() == TimeMode.Day) {
            mc.theWorld.setWorldTime(2000);
        } else if(mode.getValue() == TimeMode.Sunrise) {
            mc.theWorld.setWorldTime(22500);
        }
    }

    @EventHandler
    public void onTick(EventTick event) {
        if (mode.getValue() == TimeMode.Darkness) {
            mc.theWorld.setWorldTime(-18000);
        } else if (mode.getValue() == TimeMode.Sunset) {
            mc.theWorld.setWorldTime(-13000);
        } else if (mode.getValue() == TimeMode.Day) {
            mc.theWorld.setWorldTime(2000);
        } else if(mode.getValue() == TimeMode.Sunrise) {
            mc.theWorld.setWorldTime(22500);
        }
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        if (mode.getValue() == TimeMode.Darkness) {
            mc.theWorld.setWorldTime(-18000);
        } else if (mode.getValue() == TimeMode.Sunset) {
            mc.theWorld.setWorldTime(-13000);
        } else if (mode.getValue() == TimeMode.Day) {
            mc.theWorld.setWorldTime(2000);
        } else if(mode.getValue() == TimeMode.Sunrise) {
            mc.theWorld.setWorldTime(22500);
        }
    }

    @EventHandler
    public void onUpdate(EventPostUpdate event) {
        if (mode.getValue() == TimeMode.Darkness) {
            mc.theWorld.setWorldTime(-18000);
        } else if (mode.getValue() == TimeMode.Sunset) {
            mc.theWorld.setWorldTime(-13000);
        } else if (mode.getValue() == TimeMode.Day) {
            mc.theWorld.setWorldTime(2000);
        } else if(mode.getValue() == TimeMode.Sunrise) {
            mc.theWorld.setWorldTime(22500);
        }
    }

    @EventHandler
    public void onRecieve(EventPacketReceive event) {
        if (event.getPacket() instanceof S03PacketTimeUpdate) {
            event.setCancelled(true);
        }
    }

    public enum TimeMode {
        Darkness,
        Sunset,
        Day,
        Sunrise,
    }
}
