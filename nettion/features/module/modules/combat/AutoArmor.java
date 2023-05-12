package nettion.features.module.modules.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.utils.time.TimeHelper;

import java.util.*;

public class AutoArmor
extends Module {
    private final Option<Boolean> openInv = new Option<>("Open Inv", true);
    private final Numbers<Double> delay = new Numbers<>("Delay", 60.0, 0.0, 1000.0, 10.0);
    private final TimeHelper timeHelper = new TimeHelper();
    private final ArrayList<ItemStack> openList = new ArrayList<>();
    private final ArrayList<ItemStack> closeList = new ArrayList<>();
    private final int[] itemHelmet = new int[]{298, 302, 306, 310, 314};
    private final int[] itemChestplate = new int[]{299, 303, 307, 311, 315};
    private final int[] itemLeggings = new int[]{300, 304, 308, 312, 316};
    private final int[] itemBoots = new int[]{301, 305, 309, 313, 317};

    public AutoArmor() {
        super("AutoArmor", ModuleType.Combat);
        addValues(openInv, delay);
    }

    @EventHandler
    public void onUpdate(EventPreUpdate e) {
        if ((!this.openInv.getValue().booleanValue() || this.mc.currentScreen instanceof GuiInventory && this.openInv.getValue().booleanValue())) {
            this.clearLists();
            this.addCloseList();
            this.changeArmor();
        }
    }

    private void changeArmor() {
        String[] armorType = new String[]{"boots", "leggings", "chestplate", "helmet"};
        for (int i = 0; i < 4; ++i) {
            if (!true && !TimeHelper.isDelayComplete(this.delay.getValue().longValue())) continue;
            int bestArmor = this.getBestArmor(armorType[i]);
            if (bestArmor != -1) {
                Item currentArmor;
                if (mc.thePlayer.inventory.armorInventory[i] == null) {
                    Minecraft.playerController.windowClick(0, bestArmor, 0, 1, mc.thePlayer);
                    this.timeHelper.reset();
                    continue;
                }
                Item pBestArmor = this.getInventoryItem(bestArmor);
                if (!this.isBetter(pBestArmor, currentArmor = mc.thePlayer.inventory.armorInventory[i].getItem())) continue;
                Minecraft.playerController.windowClick(0, 8 - i, 0, 1, mc.thePlayer);
                this.timeHelper.reset();
                continue;
            }
        }
    }

    private int getBestArmor(String armorType) {
        return this.getBestInInventory(armorType);
    }

    private boolean isBetter(Item item1, Item item2) {
        if (item1 instanceof ItemArmor && item2 instanceof ItemArmor) {
            ItemArmor armor1 = (ItemArmor)item1;
            ItemArmor armor2 = (ItemArmor)item2;
            return armor1.damageReduceAmount > armor2.damageReduceAmount;
        }
        return false;
    }

    private Item getInventoryItem(int id) {
        return mc.thePlayer.inventoryContainer.getSlot(id).getStack() == null ? null : mc.thePlayer.inventoryContainer.getSlot(id).getStack().getItem();
    }

    private void addCloseList() {
        for (ItemStack st : this.openList) {
            if (!TimeHelper.isDelayComplete(1000)) continue;
            if (!this.closeList.contains(st)) {
                this.closeList.add(st);
            }
            this.openList.remove(st);
        }
    }

    private void clearLists() {
        for (ItemStack st : this.closeList) {
            ItemStack stack = null;
            InventoryPlayer invp = mc.thePlayer.inventory;
            for (int i = 0; i < 45; ++i) {
                ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (itemStack == null || st != itemStack) continue;
                stack = st;
            }
            if (stack != null) continue;
            this.closeList.remove(st);
        }
    }

    private int[] getIdsByName(String armorName) {
        switch (armorName.hashCode()) {
            case -1220934547: {
                if (!armorName.equals("helmet")) break;
                return this.itemHelmet;
            }
            case 93922241: {
                if (!armorName.equals("boots")) break;
                return this.itemBoots;
            }
            case 1069952181: {
                if (!armorName.equals("chestplate")) break;
                return this.itemChestplate;
            }
            case 1735676010: {
                if (!armorName.equals("leggings")) break;
                return this.itemLeggings;
            }
        }
        return new int[0];
    }

    private List findArmor(String armorName) {
        int[] itemIds = this.getIdsByName(armorName);
        ArrayList<Integer> availableSlots = new ArrayList<Integer>();
        for (int slots = 9; slots < mc.thePlayer.inventoryContainer.getInventory().size(); ++slots) {
            ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(slots).getStack();
            if (itemStack == null) continue;
            int itemId = Item.getIdFromItem(itemStack.getItem());
            int[] array = itemIds;
            int length = itemIds.length;
            for (int i = 0; i < length; ++i) {
                int ids = array[i];
                if (itemId != ids) continue;
                availableSlots.add(slots);
            }
        }
        return availableSlots;
    }

    private int getBestInInventory(String armorName) {
        int slot = -1;
        Iterator var4 = this.findArmor(armorName).iterator();
        while (var4.hasNext()) {
            int slots = (Integer)var4.next();
            if (slot == -1) {
                slot = slots;
            }
            if (mc.thePlayer.inventoryContainer.getSlot(slots) == null || !(mc.thePlayer.inventoryContainer.getSlot(slots).getStack().getItem() instanceof ItemArmor) || this.getValence(mc.thePlayer.inventoryContainer.getSlot(slots).getStack()) <= this.getValence(mc.thePlayer.inventoryContainer.getSlot(slot).getStack())) continue;
            slot = slots;
        }
        return slot;
    }

    private double getProtectionValue(ItemStack stack) {
        return !(stack.getItem() instanceof ItemArmor) ? 0.0 : (double)((ItemArmor)stack.getItem()).damageReduceAmount + (double)((100 - ((ItemArmor)stack.getItem()).damageReduceAmount * 4) * EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack) * 4) * 0.0075 + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack) + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack) + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack) + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack) + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.respiration.effectId, stack) + (double)EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack);
    }

    private int getValence(ItemStack itemStack) {
        int valence = 0;
        if (itemStack == null) {
            return 0;
        }
        if (itemStack.getItem() instanceof ItemArmor) {
            valence += ((ItemArmor)itemStack.getItem()).damageReduceAmount;
        }
        if (itemStack != null && itemStack.hasTagCompound()) {
            valence += (int)itemStack.getEnchantmentTagList().getCompoundTagAt(0).getDouble("lvl");
            valence += (int)itemStack.getEnchantmentTagList().getCompoundTagAt(1).getDouble("lvl");
            valence += (int)itemStack.getEnchantmentTagList().getCompoundTagAt(2).getDouble("lvl");
            valence += (int)itemStack.getEnchantmentTagList().getCompoundTagAt(3).getDouble("lvl");
            valence += (int)itemStack.getEnchantmentTagList().getCompoundTagAt(4).getDouble("lvl");
            valence += (int)itemStack.getEnchantmentTagList().getCompoundTagAt(5).getDouble("lvl");
            valence += (int)itemStack.getEnchantmentTagList().getCompoundTagAt(6).getDouble("lvl");
            valence += (int)itemStack.getEnchantmentTagList().getCompoundTagAt(7).getDouble("lvl");
            valence += (int)itemStack.getEnchantmentTagList().getCompoundTagAt(8).getDouble("lvl");
            valence += (int)itemStack.getEnchantmentTagList().getCompoundTagAt(9).getDouble("lvl");
            valence += (int)itemStack.getEnchantmentTagList().getCompoundTagAt(34).getDouble("lvl");
        }
        valence += (int)this.getProtectionValue(itemStack);
        return valence + (itemStack.getMaxDamage() - itemStack.getItemDamage());
    }
}

