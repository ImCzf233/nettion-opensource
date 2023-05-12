/*
 * Decompiled with CFR 0_132.
 */
package nettion.features.module.modules.world;

import nettion.event.EventHandler;
import nettion.features.value.values.Numbers;
import nettion.event.events.world.EventTick;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import nettion.utils.time.TimerUtils;

public class ChestStealer
extends Module {
    private Numbers<Double> delay = new Numbers<Double>("delay", 80.0, 0.0, 1000.0, 10.0);
    private TimerUtils timer = new TimerUtils();

    public ChestStealer() {
        super("ChestStealer", ModuleType.World);
        this.addValues(this.delay);
    }

    @EventHandler
    private void onUpdate(EventTick event) {
        if (this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest container = (ContainerChest)this.mc.thePlayer.openContainer;
            int i = 0;
            while (i < container.getLowerChestInventory().getSizeInventory()) {
                if (container.getLowerChestInventory().getStackInSlot(i) != null && this.timer.hasReached(this.delay.getValue())) {
                    this.mc.playerController.windowClick(container.windowId, i, 0, 1, this.mc.thePlayer);
                    this.timer.reset();
                }
                ++i;
            }
            if (this.isEmpty()) {
                this.mc.thePlayer.closeScreen();
            }
        }
    }

    private boolean isEmpty() {
        if (this.mc.thePlayer.openContainer != null && this.mc.thePlayer.openContainer instanceof ContainerChest) {
            ContainerChest container = (ContainerChest)this.mc.thePlayer.openContainer;
            int i = 0;
            while (i < container.getLowerChestInventory().getSizeInventory()) {
                ItemStack itemStack = container.getLowerChestInventory().getStackInSlot(i);
                if (itemStack != null && itemStack.getItem() != null) {
                    return false;
                }
                ++i;
            }
        }
        return true;
    }
}

