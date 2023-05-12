package nettion.features.module.modules.player;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.*;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPacketSend;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.utils.player.BlockUtil;
import nettion.utils.player.InventoryUtils;
import nettion.utils.player.PacketUtils;
import nettion.utils.player.PlayerUtils;
import nettion.utils.time.TimerUtils;

import java.util.Arrays;

public class InvCleaner extends Module {
    private final Numbers<Double> delay = new Numbers<>("Delay", 120.0, 0.0, 300.0, 10.0);
    private final Option<Boolean> onlyWhileNotMoving = new Option<>("NoMove", true);
    private final Option<Boolean> inventoryOnly = new Option<>("InventoryOnly", true);
    private final Numbers<Double> slotWeapon = new Numbers<>("Weapon Slot", 1.0, 1.0, 9.0, 1.0);
    private final Numbers<Double> slotBlock = new Numbers<>("Block Slot", 2.0, 1.0, 9.0, 1.0);
    private final Numbers<Double> slotPick = new Numbers<>("Pickaxe Slot", 3.0, 1.0, 9.0, 1.0);
    private final Numbers<Double> slotAxe = new Numbers<>("Axe Slot", 4.0, 1.0, 9.0, 1.0);
    private final Numbers<Double> slotShovel = new Numbers<>("Shovel Slot", 5.0, 1.0, 9.0, 1.0);
    private final Numbers<Double> slotBow = new Numbers<>("Bow Slot", 6.0, 1.0, 9.0, 1.0);


    private final String[] shitItems = {"tnt", "stick", "egg", "string", "cake", "mushroom", "flint", "compass", "dyePowder", "feather", "bucket", "chest", "snow", "fish", "enchant", "exp", "shears", "anvil", "torch", "seeds", "leather", "reeds", "skull", "record", "snowball", "piston"};
    private final String[] serverItems = {"selector", "tracking compass", "(right click)", "tienda ", "perfil", "salir", "shop", "collectibles", "game", "profil", "lobby", "show all", "hub", "friends only", "cofre", "(click", "teleport", "play", "exit", "hide all", "jeux", "gadget", " (activ", "emote", "amis", "bountique", "choisir", "choose "};

    private final TimerUtils timer = new TimerUtils();
    private boolean isInvOpen;

    public InvCleaner() {
        super("InvCleaner", ModuleType.Player);
        this.addValues(delay, onlyWhileNotMoving, inventoryOnly, slotWeapon, slotBlock, slotPick, slotAxe, slotShovel, slotBow);
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        if (stop()) return;
        if (!(mc.currentScreen instanceof GuiInventory) && this.inventoryOnly.getValue()) {
            return;
        }
        if (!mc.thePlayer.isUsingItem() && (mc.currentScreen == null || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiInventory || mc.currentScreen instanceof GuiIngameMenu)) {
            long delay = this.delay.getValue().longValue();
            if (timer.hasTimeElapsed(delay)) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(getDesiredSlot(ItemType.WEAPON));
                if (!slot.getHasStack() || !isBestWeapon(slot.getStack())) {
                    getBestWeapon();
                }
            }
            if (timer.hasTimeElapsed(delay)) getBestPickaxe();
            if (timer.hasTimeElapsed(delay)) getBestAxe();
            if (timer.hasTimeElapsed(delay)) getBestShovel();
            if (timer.hasTimeElapsed(delay)) {
                for (int i = 9; i < 45; i++) {
                    if (stop()) return;
                    Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                    ItemStack is = slot.getStack();
                    if (is != null && shouldDrop(is, i)) {
                        InventoryUtils.drop(i);
                        timer.reset();
                        break;
                    }
                }
            }
            if (timer.hasTimeElapsed(delay)) swapBlocks();
            if (timer.hasTimeElapsed(delay)) getBestBow();
            if (timer.hasTimeElapsed(delay)) moveArrows();
        }
    }

    @EventHandler
    private void onPacketSend(EventPacketSend e) {
        if (isInvOpen) {
            Packet<?> packet = e.getPacket();
            if ((packet instanceof C16PacketClientStatus && ((C16PacketClientStatus) packet).getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT)
                    || packet instanceof C0DPacketCloseWindow) {
                e.cancel();
            } else if (packet instanceof C02PacketUseEntity) {
                fakeClose();
            }
        }
    };

    public static float getDamageScore(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return 0;

        float damage = 0;
        Item item = stack.getItem();

        if (item instanceof ItemSword) {
            damage += ((ItemSword) item).getDamageVsEntity();
        } else if (item instanceof ItemTool) {
            damage += item.getMaxDamage();
        }

        damage += EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25F +
                EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack) * 0.1F;

        return damage;
    }

    public static float getProtScore(ItemStack stack) {
        float prot = 0;
        if (stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            prot += armor.damageReduceAmount
                    + ((100 - armor.damageReduceAmount) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)) * 0.0075F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) / 100F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) / 100F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.thorns.effectId, stack) / 100F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 25.F;
            prot += EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) / 100F;
        }
        return prot;
    }

    private int getDesiredSlot(ItemType tool) {
        int slot = 36;
        switch (tool) {
            case WEAPON:
                slot =  (slotWeapon.getValue().intValue());
                break;
            case PICKAXE:
                slot =  (slotPick.getValue().intValue());
                break;
            case AXE:
                slot =  (slotAxe.getValue().intValue());
                break;
            case SHOVEL:
                slot =  (slotShovel.getValue().intValue());
                break;
            case BLOCK:
                slot =  (slotBlock.getValue().intValue());
                break;
            case BOW:
                slot =  (slotBow.getValue().intValue());
                break;
        }
        return slot + 35;
    }

    private boolean isBestWeapon(ItemStack is) {
        float damage = getDamageScore(is);
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is2 = slot.getStack();
                if (getDamageScore(is2) > damage && is2.getItem() instanceof ItemSword) {
                    return false;
                }
            }
        }
        return is.getItem() instanceof ItemSword;
    }

    private void getBestWeapon() {
        for (int i = 9; i < 45; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (isBestWeapon(is) && getDamageScore(is) > 0 && is.getItem() instanceof ItemSword) {
                    swap(i, getDesiredSlot(ItemType.WEAPON) - 36);
                    break;
                }
            }
        }
    }

    private boolean shouldDrop(ItemStack stack, int slot) {
        String stackName = stack.getDisplayName().toLowerCase();
        Item item = stack.getItem();
        String ulName = item.getUnlocalizedName();
        if (Arrays.stream(serverItems).anyMatch(stackName::contains)) return false;

        if (item instanceof ItemBlock) {
            return !BlockUtil.isValidBlock(((ItemBlock) item).getBlock(), true);
        }

        int weaponSlot = getDesiredSlot(ItemType.WEAPON);
        int pickaxeSlot = getDesiredSlot(ItemType.PICKAXE);
        int axeSlot = getDesiredSlot(ItemType.AXE);
        int shovelSlot = getDesiredSlot(ItemType.SHOVEL);

        if ((slot != weaponSlot || !isBestWeapon(mc.thePlayer.inventoryContainer.getSlot(weaponSlot).getStack()))
                && (slot != pickaxeSlot || !isBestPickaxe(mc.thePlayer.inventoryContainer.getSlot(pickaxeSlot).getStack()))
                && (slot != axeSlot || !isBestAxe(mc.thePlayer.inventoryContainer.getSlot(axeSlot).getStack()))
                && (slot != shovelSlot || !isBestShovel(mc.thePlayer.inventoryContainer.getSlot(shovelSlot).getStack()))) {
            if (item instanceof ItemArmor) {
                for (int type = 1; type < 5; type++) {
                    if (mc.thePlayer.inventoryContainer.getSlot(4 + type).getHasStack()) {
                        ItemStack is = mc.thePlayer.inventoryContainer.getSlot(4 + type).getStack();
                        if (isBestArmor(is, type)) {
                            continue;
                        }
                    }
                    if (isBestArmor(stack, type)) {
                        return false;
                    }
                }
            }

            if ((item == Items.wheat) || item == Items.spawn_egg || (item instanceof ItemPotion && isShitPotion(stack))) {
                return true;
            } else if (!(item instanceof ItemSword) && !(item instanceof ItemTool) && !(item instanceof ItemHoe) && !(item instanceof ItemArmor)) {
                return item instanceof ItemGlassBottle || Arrays.stream(shitItems).anyMatch(ulName::contains);
            }
            return true;
        }

        return false;
    }

    private void getBestPickaxe() {
        for (int i = 9; i < 45; ++i) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();
                if (isBestPickaxe(is) && !isBestWeapon(is)) {
                    int desiredSlot = getDesiredSlot(ItemType.PICKAXE);
                    if (i == desiredSlot) return;
                    Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(desiredSlot);
                    if (!slot2.getHasStack() || !isBestPickaxe(slot2.getStack())) {
                        swap(i, desiredSlot - 36);
                    }
                }
            }
        }
    }

    private void getBestAxe() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();
                if (isBestAxe(is) && !isBestWeapon(is)) {
                    int desiredSlot = getDesiredSlot(ItemType.AXE);
                    if (i == desiredSlot) return;
                    Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(desiredSlot);
                    if (!slot2.getHasStack() || !isBestAxe(slot2.getStack())) {
                        swap(i, desiredSlot - 36);
                        timer.reset();
                    }
                }
            }
        }
    }

    private void getBestShovel() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();
                if (isBestShovel(is) && !isBestWeapon(is)) {
                    int desiredSlot = getDesiredSlot(ItemType.SHOVEL);
                    if (i == desiredSlot) return;
                    Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(desiredSlot);
                    if (!slot2.getHasStack() || !isBestShovel(slot2.getStack())) {
                        swap(i, desiredSlot - 36);
                        timer.reset();
                    }
                }
            }
        }
    }

    private void getBestBow() {
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            if (slot.getHasStack()) {
                ItemStack is = slot.getStack();
                String stackName = is.getDisplayName().toLowerCase();
                if (Arrays.stream(serverItems).anyMatch(stackName::contains) || !(is.getItem() instanceof ItemBow))
                    continue;
                if (isBestBow(is) && !isBestWeapon(is)) {
                    int desiredSlot = getDesiredSlot(ItemType.BOW);
                    if (i == desiredSlot) return;
                    Slot slot2 = mc.thePlayer.inventoryContainer.getSlot(desiredSlot);
                    if (!slot2.getHasStack() || !isBestBow(slot2.getStack())) {
                        swap(i, desiredSlot - 36);
                    }
                }
            }
        }
    }

    private void moveArrows() {
        for (int i = 36; i < 45; i++) {
            ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (is != null && is.getItem() == Items.arrow) {
                for (int j = 0; j < 36; j++) {
                    if (mc.thePlayer.inventoryContainer.getSlot(j).getStack() == null) {
                        fakeOpen();
                        InventoryUtils.click(i, 0, true);
                        fakeClose();
                        timer.reset();
                        break;
                    }
                }
            }
        }
    }

    private int getMostBlocks() {
        int stack = 0;
        int biggestSlot = -1;
        for (int i = 9; i < 45; i++) {
            Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
            ItemStack is = slot.getStack();
            if (is != null && is.getItem() instanceof ItemBlock && is.stackSize > stack && Arrays.stream(serverItems).noneMatch(is.getDisplayName().toLowerCase()::contains)) {
                stack = is.stackSize;
                biggestSlot = i;
            }
        }
        return biggestSlot;
    }

    private void swapBlocks() {
        int mostBlocksSlot = getMostBlocks();
        int desiredSlot = getDesiredSlot(ItemType.BLOCK);
        if (mostBlocksSlot != -1 && mostBlocksSlot != desiredSlot) {
            // only switch if the hotbar slot doesn't already have blocks of the same quantity to prevent an inf loop
            Slot dss = mc.thePlayer.inventoryContainer.getSlot(desiredSlot);
            ItemStack dsis = dss.getStack();
            if (!(dsis != null && dsis.getItem() instanceof ItemBlock && dsis.stackSize >= mc.thePlayer.inventoryContainer.getSlot(mostBlocksSlot).getStack().stackSize && Arrays.stream(serverItems).noneMatch(dsis.getDisplayName().toLowerCase()::contains))) {
                swap(mostBlocksSlot, desiredSlot - 36);
            }
        }
    }

    private boolean isBestPickaxe(ItemStack stack) {
        Item item = stack.getItem();
        if (!(item instanceof ItemPickaxe)) {
            return false;
        } else {
            float value = getToolScore(stack);
            for (int i = 9; i < 45; i++) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (is.getItem() instanceof ItemPickaxe && getToolScore(is) > value) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private boolean isBestShovel(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemSpade)) {
            return false;
        } else {
            float score = getToolScore(stack);
            for (int i = 9; i < 45; i++) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (is.getItem() instanceof ItemSpade && getToolScore(is) > score) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private boolean isBestAxe(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemAxe)) {
            return false;
        } else {
            float value = getToolScore(stack);
            for (int i = 9; i < 45; i++) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (getToolScore(is) > value && is.getItem() instanceof ItemAxe && !isBestWeapon(stack)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private boolean isBestBow(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemBow)) {
            return false;
        } else {
            float value = getPowerLevel(stack);
            for (int i = 9; i < 45; i++) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (getPowerLevel(is) > value && is.getItem() instanceof ItemBow && !isBestWeapon(stack)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private float getPowerLevel(ItemStack stack) {
        float score = 0;
        Item item = stack.getItem();
        if (item instanceof ItemBow) {
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack) * 0.5F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) * 0.1F;
        }
        return score;
    }

    private float getToolScore(ItemStack stack) {
        float score = 0;
        Item item = stack.getItem();
        if (item instanceof ItemTool) {
            ItemTool tool = (ItemTool) item;
            String name = item.getUnlocalizedName().toLowerCase();
            if (item instanceof ItemPickaxe) {
                score = tool.getStrVsBlock(stack, Blocks.stone) - (name.contains("gold") ? 5 : 0);
            } else if (item instanceof ItemSpade) {
                score = tool.getStrVsBlock(stack, Blocks.dirt) - (name.contains("gold") ? 5 : 0);
            } else {
                if (!(item instanceof ItemAxe)) return 1;
                score = tool.getStrVsBlock(stack, Blocks.log) - (name.contains("gold") ? 5 : 0);
            }
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, stack) * 0.0075F;
            score += EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack) / 100F;
        }
        return score;
    }

    private boolean isShitPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            ItemPotion pot = (ItemPotion) stack.getItem();
            if (pot.getEffects(stack) == null) return true;
            for (PotionEffect effect : pot.getEffects(stack)) {
                if (effect.getPotionID() == Potion.moveSlowdown.getId()
                        || effect.getPotionID() == Potion.weakness.getId()
                        || effect.getPotionID() == Potion.poison.getId()
                        || effect.getPotionID() == Potion.harm.getId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isBestArmor(ItemStack stack, int type) {
        String typeStr = "";
        switch (type) {
            case 1:
                typeStr = "helmet";
                break;
            case 2:
                typeStr = "chestplate";
                break;
            case 3:
                typeStr = "leggings";
                break;
            case 4:
                typeStr = "boots";
                break;
        }
        if (stack.getUnlocalizedName().contains(typeStr)) {
            float prot = getProtScore(stack);
            for (int i = 5; i < 45; i++) {
                Slot slot = mc.thePlayer.inventoryContainer.getSlot(i);
                if (slot.getHasStack()) {
                    ItemStack is = slot.getStack();
                    if (is.getUnlocalizedName().contains(typeStr) && getProtScore(is) > prot) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    boolean inventoryPackets = true;

    private void fakeOpen() {
        if (!isInvOpen) {
            timer.reset();
            if (!inventoryOnly.getValue() && inventoryPackets)
                PacketUtils.sendPacketNoEvent(new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
            isInvOpen = true;
        }
    }

    private void fakeClose() {
        if (isInvOpen) {
            if (!inventoryOnly.getValue() && inventoryPackets)
                PacketUtils.sendPacketNoEvent(new C0DPacketCloseWindow(mc.thePlayer.inventoryContainer.windowId));
            isInvOpen = false;
        }
    }

    private void swap(int slot, int hSlot) {
        fakeOpen();
        InventoryUtils.swap(slot, hSlot);
        fakeClose();
        timer.reset();
    }

    private boolean stop() {
        return (inventoryOnly.getValue() && !(mc.currentScreen instanceof GuiInventory)) || (onlyWhileNotMoving.getValue() && PlayerUtils.isMoving());
    }

    private enum ItemType {
        WEAPON, PICKAXE, AXE, SHOVEL, BLOCK, BOW
    }

}
