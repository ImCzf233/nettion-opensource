/*
 * Decompiled with CFR 0_132.
 */
package nettion.features.command.commands;

import nettion.features.module.Module;
import nettion.features.module.ModuleManager;
import nettion.utils.Helper;
import nettion.features.command.Command;
import net.minecraft.util.EnumChatFormatting;

public class Toggle
extends Command {
    public Toggle() {
        super("toggle", new String[]{"toggle", "togl", "turnon", "enable"}, "", "Toggles a specified Module");
    }

    @Override
    public String execute(String[] args) {
        String modName = "";
        if (args.length > 1) {
            modName = args[1];
        } else if (args.length < 1) {
            Helper.sendMessageWithoutPrefix("\u00a7bCorrect usage:\u00a77 .t <module>");
        }
        boolean found = false;
        Module m = ModuleManager.getModuleByName(args[0].replaceAll(" ",""));
        if (m != null) {
            if (!m.isEnabled()) {
                m.setEnabled(true);
            } else {
                m.setEnabled(false);
            }
            found = true;
            if (m.isEnabled()) {
                Helper.sendMessage("> " + m.getName() + (Object)((Object)EnumChatFormatting.GRAY) + " was" + (Object)((Object)EnumChatFormatting.GREEN) + " enabled");
            } else {
                Helper.sendMessage("> " + m.getName() + (Object)((Object)EnumChatFormatting.GRAY) + " was" + (Object)((Object)EnumChatFormatting.RED) + " disabled");
            }
        }
        if (!found) {
            Helper.sendMessage("> Module name " + (Object)((Object)EnumChatFormatting.RED) + args[0] + (Object)((Object)EnumChatFormatting.GRAY) + " is invalid");
        }
        return null;
    }
}

