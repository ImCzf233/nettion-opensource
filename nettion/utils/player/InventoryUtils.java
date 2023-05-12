package nettion.utils.player;

import net.minecraft.client.Minecraft;

public class InventoryUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static void click(int slot, int mouseButton, boolean shiftClick) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, mouseButton, shiftClick ? 1 : 0, mc.thePlayer);
    }

    public static void drop(int slot) {
        mc.playerController.windowClick(0, slot, 1, 4, mc.thePlayer);
    }

    public static void swap(int slot, int hSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot, hSlot, 2, mc.thePlayer);
    }

    public static int findEmptySlot() {
        for (int i = 0; i < 8; i++) {
            if (mc.thePlayer.inventory.mainInventory[i] == null)
                return i;
        }

        return mc.thePlayer.inventory.currentItem + (mc.thePlayer.inventory.getCurrentItem() == null ? 0 : ((mc.thePlayer.inventory.currentItem < 8) ? 4 : -1));
    }

    // TODO: AutoPot refill always put potions on slot 1, bugs here?
    public static int findEmptySlot(int priority) {
        if (mc.thePlayer.inventory.mainInventory[priority] == null)
            return priority;

        return findEmptySlot();
    }
}
