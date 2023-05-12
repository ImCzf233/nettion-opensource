package nettion.features.module.modules.player;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.util.Timer;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import net.minecraft.network.play.client.C03PacketPlayer;
import nettion.features.value.values.Mode;
import nettion.utils.player.BlockUtil;
import nettion.utils.player.PacketUtils;

public class FastUse
extends Module {
    private final Mode<Enum> mode = new Mode<>("Mode", modes.values(), modes.Packet);
    private boolean usedTimer = false;
    private boolean canBoost = false;

    public FastUse() {
        super("FastUse", ModuleType.Player);
        addValues(mode);
    }

    @Override
    public void onDisable() {
        if (usedTimer) {
            Timer.timerSpeed = 1F;
            usedTimer = false;
        }
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        if (mode.getValue() == modes.Packet) {
            if (mc.thePlayer.isEating()) {
                for (int i = 0; i < (100 / 2); i++) {
                    PacketUtils.sendPacket(new C03PacketPlayer(mc.thePlayer.onGround));
                    PacketUtils.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
                }
            }
        } else if (mode.getValue() == modes.GrimAC) {
            if (!mc.thePlayer.isUsingItem()) {
                Timer.timerSpeed = 1.0F;
                return;
            }

            Item usingItem = mc.thePlayer.getItemInUse().getItem();

            if (usingItem instanceof ItemFood ||usingItem instanceof ItemBucketMilk || usingItem instanceof ItemPotion){
                Timer.timerSpeed = 0.5F;
                usedTimer = true;
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
            }
        } else if (mode.getValue() == modes.Timer) {
            if (mc.thePlayer.isEating()) {
                Timer.timerSpeed = 1.5f;
            } else if(Timer.timerSpeed == 1.5f) {
                Timer.timerSpeed = 1.0f;
            }
        } else if (mode.getValue() == modes.NCP) {
            if (mc.thePlayer.getItemInUseDuration() == 17)
                canBoost = true;
            if ((mc.thePlayer.onGround) && (!(!mc.thePlayer.isInWater() && BlockUtil.isInLiquid())) && canBoost) {
                canBoost = false;
                for (int i = 0; i < 20; ++i) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                }
            }
        } else if (mode.getValue() == modes.Matrix) {
            mc.timer.timerSpeed = 0.5f;
            usedTimer = true;
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
        }
    }

    enum modes {
        Packet,
        Matrix,
        GrimAC,
        Timer,
        NCP
    }
}


