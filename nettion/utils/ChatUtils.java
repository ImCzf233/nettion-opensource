/*
 * Decompiled with CFR 0_132.
 */
package nettion.utils;

import nettion.features.module.Module;
import nettion.features.module.ModuleManager;
import nettion.Nettion;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class ChatUtils {
    private final ChatComponentText message;

    private ChatUtils(ChatComponentText message) {
        this.message = message;
    }

    public void displayClientSided() {
        Minecraft.getMinecraft().thePlayer.addChatMessage(this.message);
    }

    private ChatComponentText getChatComponent() {
        return this.message;
    }

    /* synthetic */ ChatUtils(ChatComponentText chatComponentText, ChatUtils chatUtils) {
        this(chatComponentText);
    }

    public static class ChatMessageBuilder {
        private static final EnumChatFormatting defaultMessageColor = EnumChatFormatting.WHITE;
        private ChatComponentText theMessage = new ChatComponentText("");
        private boolean useDefaultMessageColor = false;
        private ChatStyle workingStyle = new ChatStyle();
        private ChatComponentText workerMessage = new ChatComponentText("");

        public ChatMessageBuilder(boolean prependDefaultPrefix, boolean useDefaultMessageColor) {
            if (prependDefaultPrefix) {
                Nettion.instance.getClass();
                this.theMessage.appendSibling(new ChatMessageBuilder(false, false).appendText(String.valueOf((Object)((Object)EnumChatFormatting.AQUA) + "Nettion" + " ")).setColor(EnumChatFormatting.RED).build().getChatComponent());
            }
            this.useDefaultMessageColor = useDefaultMessageColor;
        }

        public ChatMessageBuilder appendText(String text) {
            this.appendSibling();
            this.workerMessage = new ChatComponentText(text);
            this.workingStyle = new ChatStyle();
            if (this.useDefaultMessageColor) {
                this.setColor(defaultMessageColor);
            }
            return this;
        }

        public ChatMessageBuilder setColor(EnumChatFormatting color) {
            this.workingStyle.setColor(color);
            return this;
        }

        public ChatMessageBuilder italic() {
            this.workingStyle.setItalic(true);
            return this;
        }

        public ChatUtils build() {
            this.appendSibling();
            return new ChatUtils(this.theMessage, null);
        }

        private void appendSibling() {
            this.theMessage.appendSibling(this.workerMessage.setChatStyle(this.workingStyle));
        }
    }

    public static void debug(Object msg) {
        if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null)
            return;

        String className = "Unknown";
        StackTraceElement[] mStacks = Thread.currentThread().getStackTrace();
        for (Module module : ModuleManager.getModules()) {
            for (StackTraceElement mStack : mStacks) {
                if (module.getClass().getName().equals(mStack.getClassName()) && !module.getName().equals("Debug")) {
                    className = module.getName();
                    break;
                }
            }
        }


        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("\247d[" + className + "]\247r " + EnumChatFormatting.GRAY + msg));
        } else {
            System.out.println("[DEBUG] " + "\247d[" + className + "]\247r " + EnumChatFormatting.GRAY + msg);
        }
    }
}

