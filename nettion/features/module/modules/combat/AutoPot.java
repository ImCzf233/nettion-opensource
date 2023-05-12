package nettion.features.module.modules.combat;

import nettion.Nettion;
import nettion.features.module.modules.movement.Scaffold;
import nettion.features.value.values.Numbers;
import nettion.event.EventHandler;
import nettion.event.events.world.EventMove;
import nettion.event.events.world.EventPostUpdate;
import nettion.event.events.world.EventPreUpdate;
import nettion.event.events.world.EventTick;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.player.BlockUtil;
import nettion.utils.player.InventoryUtils;
import nettion.utils.player.PlayerUtils;
import net.minecraft.block.BlockGlass;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import nettion.features.value.values.Option;
import nettion.utils.time.TimerUtils;

import java.util.List;

public class AutoPot extends Module {
    private boolean jumping;
    private boolean rotated;
    public static Numbers<Double> health = new Numbers<>("Health", 13.0, 1.0, 20.0, 1.0);
    public static Numbers<Double> delay = new Numbers<>("Delay", 500.0, 100.0, 1500.0, 50.0);
    public static Option<Boolean> jump = new Option("Jump", false);
    public static Option<Boolean> regen = new Option("Regen Pot", true);
    public static Option<Boolean> heal = new Option("Heal Pot", true);
    public static Option<Boolean> speed = new Option("Speed Pot", false);
    public static Option<Boolean> nofrog = new Option("No Frog", true);

    public AutoPot() {
        super("AutoPot", ModuleType.Combat);
        addValues(health, delay, jump, regen, heal, speed, nofrog);
    }

    public static TimerUtils timer = new TimerUtils();
    private TimerUtils cooldown = new TimerUtils();

    private int lastPottedSlot;

    @EventHandler
    private void onMove(final EventMove event) {
        if (this.jumping) {
            this.mc.thePlayer.motionX = 0;
            this.mc.thePlayer.motionZ = 0;
            event.x = 0;
            event.z = 0;

            if (cooldown.hasPassed(100) && this.mc.thePlayer.onGround) {
                this.jumping = false;
            }
        }
    }

    @EventHandler
    private void onPreUpdate(final EventPreUpdate event) {
        if (PlayerUtils.getBlockUnderPlayer(mc.thePlayer, 0.01) instanceof BlockGlass || !PlayerUtils.isOnGround(0.01))  {
            timer.reset();
            return;
        }

        if (mc.thePlayer.openContainer != null) {
            if (mc.thePlayer.openContainer instanceof ContainerChest) {
                timer.reset();
                return;
            }
        }

        if (Nettion.instance.getModuleManager().getModuleByClass(Scaffold.class).isEnabled())
            return;

        if (Killaura.target != null) {
            rotated = false;
            timer.reset();
            return;
        }

        final int potSlot = this.getPotFromInventory();
        if (potSlot != -1 && timer.hasPassed(delay.getValue())) {
            if (jump.getValue() && !BlockUtil.isInLiquid()) {
                event.setPitch(-89.5f);

                this.jumping = true;
                if (this.mc.thePlayer.onGround) {
                    this.mc.thePlayer.jump();
                    cooldown.reset();
                }
            } else {
                event.setPitch(89.5f);
            }

            rotated = true;
        }
    }

    @EventHandler
    private void onPostUpdate(final EventPostUpdate event) {
        if (!rotated)
            return;

        rotated = false;

        final int potSlot = this.getPotFromInventory();
        if (potSlot != -1 && timer.hasPassed(delay.getValue()) && mc.thePlayer.isCollidedVertically) {
            final int prevSlot = mc.thePlayer.inventory.currentItem;
            if (potSlot < 9) {
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(potSlot));
                mc.thePlayer.sendQueue.addToSendQueue(
                        new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(prevSlot));
                mc.thePlayer.inventory.currentItem = prevSlot;
                timer.reset();

                this.lastPottedSlot = potSlot;
            }
        }
    }

    // Auto Refill
    @EventHandler
    public void onTick(EventTick event) {
        if (this.mc.currentScreen != null)
            return;

        final int potSlot = this.getPotFromInventory();
        if (potSlot != -1 && potSlot > 8 && this.mc.thePlayer.ticksExisted % 4 == 0) {
            this.swap(potSlot, InventoryUtils.findEmptySlot(this.lastPottedSlot));
        }
    }

    private void swap(final int slot, final int hotbarNum) {
        this.mc.playerController.windowClick(this.mc.thePlayer.inventoryContainer.windowId, slot, hotbarNum, 2,
                this.mc.thePlayer);
    }

    private int getPotFromInventory() {
        // heals
        for (int i = 0; i < 36; ++i) {
            if (mc.thePlayer.inventory.mainInventory[i] != null) {
                final ItemStack is = mc.thePlayer.inventory.mainInventory[i];
                final Item item = is.getItem();

                if (!(item instanceof ItemPotion)) {
                    continue;
                }

                ItemPotion pot = (ItemPotion) item;

                if (!ItemPotion.isSplash(is.getMetadata())) {
                    continue;
                }

                List<PotionEffect> effects = pot.getEffects(is);

                for (PotionEffect effect : effects) {
                    if (mc.thePlayer.getHealth() < health.getValue() && ((heal.getValue() && effect.getPotionID() == Potion.heal.id) || (regen.getValue() && effect.getPotionID() == Potion.regeneration.id && !hasEffect(Potion.regeneration.id))))
                        return i;
                }
            }
        }

        // others
        for (int i = 0; i < 36; ++i) {
            if (this.mc.thePlayer.inventory.mainInventory[i] != null) {
                final ItemStack is = this.mc.thePlayer.inventory.mainInventory[i];
                final Item item = is.getItem();

                if (!(item instanceof ItemPotion)) {
                    continue;
                }

                List<PotionEffect> effects = ((ItemPotion) item).getEffects(is);

                for (PotionEffect effect : effects) {
                    if (effect.getPotionID() == Potion.moveSpeed.id && speed.getValue()
                            && !hasEffect(Potion.moveSpeed.id))
                        if (!is.getDisplayName().contains("\247a") || !nofrog.getValue())
                            return i;
                }
            }
        }

        return -1;
    }

    private boolean hasEffect(int potionId) {
        for (PotionEffect item : mc.thePlayer.getActivePotionEffects()) {
            if (item.getPotionID() == potionId)
                return true;
        }
        return false;
    }
}

