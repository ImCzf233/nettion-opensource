package nettion.features.module.modules.player;

import net.minecraft.client.Minecraft;
import nettion.event.EventHandler;
import nettion.features.value.values.Option;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import nettion.event.events.world.EventPacketSend;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

import java.util.Objects;

public class AutoTool extends Module {
    public Option<Boolean> autoweapon = new Option<Boolean>("Auto Weapon", true);
    public AutoTool() {
        super("AutoTool", ModuleType.Player);
        addValues(this.autoweapon);
    }

    @EventHandler
    public void onAttack(EventPacketSend e) {
        if(!autoweapon.getValue()) return;
        if(e.getPacket() instanceof C02PacketUseEntity){
            if(((C02PacketUseEntity)e.getPacket()).getAction().equals(C02PacketUseEntity.Action.ATTACK)){
                boolean checks = !mc.thePlayer.isEating();
                if (checks) bestSword(((C02PacketUseEntity)e.getPacket()).getEntityFromWorld(mc.theWorld));
            }
        }
    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        boolean checks = !mc.thePlayer.isEating();
        if (checks && Minecraft.playerController.isHittingBlock && !Objects.isNull(mc.objectMouseOver.getBlockPos())) {
            bestTool(mc.objectMouseOver.getBlockPos().getX(), mc.objectMouseOver.getBlockPos().getY(), mc.objectMouseOver.getBlockPos().getZ());
        }
    }

    public void bestSword(Entity targetEntity) {
        int bestSlot = 0;
        float f = (1.0F / -1.0F);
        for (int i1 = 36; i1 < 45; i1++) {
            if ((mc.thePlayer.inventoryContainer.inventorySlots.toArray()[i1] != null) && (targetEntity != null))
            {
                ItemStack curSlot = mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                if ((curSlot != null) &&
                        ((curSlot.getItem() instanceof ItemSword)))
                {
                    ItemSword sword = (ItemSword)curSlot.getItem();
                    if (sword.getDamageVsEntity() > f)
                    {
                        bestSlot = i1 - 36;
                        f = sword.getDamageVsEntity();
                    }
                }
            }
        }
        if (f > (1.0F / -1.0F)) {

            mc.thePlayer.inventory.currentItem = bestSlot;
            mc.playerController.updateController();
        }
    }

    public void bestTool(int x, int y, int z) {
        int blockId = Block.getIdFromBlock(mc.theWorld.getBlockState(new net.minecraft.util.BlockPos(x, y, z)).getBlock());
        int bestSlot = 0;
        float f = -1.0F;
        for (int i1 = 36; i1 < 45; i1++) {
            try
            {
                ItemStack curSlot = mc.thePlayer.inventoryContainer.getSlot(i1).getStack();
                if ((((curSlot.getItem() instanceof net.minecraft.item.ItemTool)) || ((curSlot.getItem() instanceof ItemSword)) || ((curSlot.getItem() instanceof net.minecraft.item.ItemShears))) &&
                        (curSlot.getStrVsBlock(Block.getBlockById(blockId)) > f))
                {
                    bestSlot = i1 - 36;
                    f = curSlot.getStrVsBlock(Block.getBlockById(blockId));
                }
            }
            catch (Exception localException) {}
        }

        if (f != -1.0F) {
            mc.thePlayer.inventory.currentItem = bestSlot;
            mc.playerController.updateController();
        }
    }
}
