package nettion.features.module.modules.combat;

import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.time.TimerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.network.play.client.C02PacketUseEntity;
import nettion.event.EventHandler;

public class AntiFireball extends Module {
    TimerUtils time = new TimerUtils();
    public AntiFireball() {
        super("AntiFireball", ModuleType.Combat);
    }

    @EventHandler
    @SuppressWarnings("unused")
    public void onUpdate(EventPreUpdate event) {
        for (Entity entity : getEntityList()) {
            if (entity instanceof EntityFireball && mc.thePlayer.getDistanceToEntity(entity) < 4.5) {
                if (time.hasTimeElapsed(30)) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
                    mc.thePlayer.swingItem();
                }
            }
        }
    }

    public Entity[] getEntityList() {
        return mc.theWorld != null ? mc.theWorld.getLoadedEntityList().toArray(new Entity[0]) : null;
    }
}
