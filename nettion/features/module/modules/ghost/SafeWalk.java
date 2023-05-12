/*
 * Decompiled with CFR 0_132.
 */
package nettion.features.module.modules.ghost;

import nettion.features.value.values.Option;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public class SafeWalk extends Module {
    public static final Option<Boolean> sneak = new Option<>("Sneak", true);
    public SafeWalk() {
        super("SafeWalk", ModuleType.Ghost);
        addValues(sneak);
    }

    public Block getBlock(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock();
    }

    public Block getBlockUnderPlayer(EntityPlayer player) {
        return getBlock(new BlockPos(player.posX , player.posY - 1.0d, player.posZ));
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        if (sneak.getValue()) {
            if(getBlockUnderPlayer(mc.thePlayer) instanceof BlockAir) {
                if(mc.thePlayer.onGround) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                }
            } else {
                if(mc.thePlayer.onGround) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        if (sneak.getValue()) {
            mc.thePlayer.setSneaking(false);
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (sneak.getValue()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
        super.onDisable();
    }
}

