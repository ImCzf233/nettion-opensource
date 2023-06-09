package nettion.features.module.modules.combat;

import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.time.TimerUtils;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Mouse;

public class AutoHead extends Module {
    private boolean eatingApple;
    private int switched = -1;
    public static boolean doingStuff = false;
    private final TimerUtils timer = new TimerUtils();
    private final Option<Boolean> eatHeads = new Option("eatheads", true);
    private final Option<Boolean> eatApples = new Option("eatapples", true);
    private final Numbers<Double> health = new Numbers("health", 10.0, 1.0, 20.0, 1.0);
    private final Numbers<Double> delay = new Numbers("delay", 10.0, 0.0, 100.0, 5.0);

    public AutoHead() {
        super("AutoHead", ModuleType.Combat);
        addValues(health, delay, eatApples, eatHeads);
    }

    public void onEnable() {
        this.eatingApple = doingStuff = false;
        switched = -1;
        timer.reset();
        super.onEnable();
    }

    public void onDisable() {
        doingStuff = false;
        if (eatingApple) {
            repairItemPress();
            repairItemSwitch();
        }
        super.onDisable();
    }

    private void repairItemPress() {
        if (mc.gameSettings != null) {
            final KeyBinding keyBindUseItem = mc.gameSettings.keyBindUseItem;
            if (keyBindUseItem != null) keyBindUseItem.setPressed(false);
        }
    }


    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        this.setSuffix(health.getValue());
        if (mc.thePlayer == null) return;
        final InventoryPlayer inventory = mc.thePlayer.inventory;
        if (inventory == null) return;
        doingStuff = false;
        if (!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)) {
            final KeyBinding useItem = mc.gameSettings.keyBindUseItem;

            if (!this.timer.hasReached(delay.getValue())) {
                eatingApple = false;
                repairItemPress();
                repairItemSwitch();
                return;
            }

            if (mc.thePlayer.capabilities.isCreativeMode || mc.thePlayer.isPotionActive(Potion.regeneration) ||mc.thePlayer.getHealth() >= health.getValue()) {
                this.timer.reset();
                if (eatingApple) {
                    eatingApple = false;
                    repairItemPress();
                    repairItemSwitch();
                }
                return;
            }

            for (int i = 0; i < 2; i++) {
                final boolean doEatHeads = i != 0;

                if (doEatHeads) {
                    if (!this.eatHeads.getValue()) continue;
                } else {
                    if (!this.eatApples.getValue()) {
                        eatingApple = false;
                        repairItemPress();
                        repairItemSwitch();
                        continue;
                    }
                }

                int slot;

                if (doEatHeads) {
                    slot = this.getItemFromHotbar(397);
                } else {
                    slot = this.getItemFromHotbar(322);
                }

                if (slot == -1) continue;

                final int tempSlot = inventory.currentItem;

                doingStuff = true;
                if (doEatHeads) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(inventory.getCurrentItem()));
                    mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(tempSlot));
                    timer.reset();
                } else {
                    inventory.currentItem = slot;
                    useItem.setPressed(true);
                    if (eatingApple) continue; // no message spam
                    eatingApple = true;
                    this.switched = tempSlot;
                }


            }
        }
    }

    private void repairItemSwitch() {
        final EntityPlayerSP p = mc.thePlayer;
        if (p == null) return;
        final InventoryPlayer inventory = p.inventory;
        if (inventory == null) return;
        int switched = this.switched;
        if (switched == -1) return;
        inventory.currentItem = switched;
        switched = -1;
        this.switched = switched;
    }

    private int getItemFromHotbar(final int id) {
        for (int i = 0; i < 9; i++) {
            if (mc.thePlayer.inventory.mainInventory[i] != null) {
                final ItemStack is = mc.thePlayer.inventory.mainInventory[i];
                final Item item = is.getItem();
                if (Item.getIdFromItem(item) == id) {
                    return i;
                }
            }
        }
        return -1;
    }
}
