package nettion.features.module.modules.movement;

import net.minecraft.item.ItemSword;
import nettion.event.events.world.EventPostUpdate;
import nettion.features.value.values.Mode;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import net.minecraft.util.BlockPos;
import net.minecraft.network.play.client.*;
import nettion.utils.player.PacketUtils;
import nettion.utils.time.TimerUtils;

public class NoSlow extends Module {
    public static Mode<Enum> mode = new Mode("mode", mods.values(), mods.Watchdog);

    public NoSlow() {
        super("NoSlow", ModuleType.Movement);
        addValues(mode);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {
    }


    @EventHandler
    private void onUpdate(EventPreUpdate event) {
        this.setSuffix(mode.getValue());
        if (mode.getValue() == mods.GrimAC) {
            if (mc.thePlayer.isMoving() && mc.thePlayer.isUsingItem()) {
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange((mc.thePlayer.inventory.currentItem + 1) % 9));
                mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        } else if (mode.getValue() == mods.Watchdog) {
            if (mc.thePlayer.isMoving() && mc.thePlayer.isUsingItem() && !mc.thePlayer.isEating()) {
                PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        }
    }

    @EventHandler
    private void onPost(EventPostUpdate event) {
        if (mode.getValue() == mods.GrimAC) {
            if (mc.thePlayer.isMoving() && (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking())) {
                mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0, 0, 0));
            }
        } else if (mode.getValue() == mods.Watchdog) {
            if (mc.thePlayer.isMoving() && mc.thePlayer.isUsingItem() && mc.thePlayer.isEating()) {
                BlockPos pos = new BlockPos(-1, -1, -1);
                PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(pos, 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
            }
        } else if (mode.getValue() == mods.AAC5) {
            BlockPos pos = new BlockPos(-1, -1, -1);
            if (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking()) {
                PacketUtils.sendPacket(new C08PacketPlayerBlockPlacement(pos, 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
            }
        }
    }


    public enum mods {
        Vanilla,
        Watchdog,
        GrimAC,
        AAC5,
    }
}