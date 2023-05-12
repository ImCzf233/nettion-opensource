/*
 * Decompiled with CFR 0_132.
 */
package nettion.features.command.commands;

import nettion.features.command.Command;
import nettion.utils.Helper;
import nettion.utils.math.MathUtil;
import net.minecraft.util.EnumChatFormatting;

public class VClip
extends Command {
    public VClip() {
        super("Vc", new String[]{"Vclip", "clip", "verticalclip", "clip"}, "", "Teleport down a specific ammount");
    }

    @Override
    public String execute(String[] args) {
        if (args.length > 0) {
            if (MathUtil.parsable(args[0], (byte)4)) {
                float distance = Float.parseFloat(args[0]);
                Helper.mc.thePlayer.setPosition(Helper.mc.thePlayer.posX, Helper.mc.thePlayer.posY + (double)distance, Helper.mc.thePlayer.posZ);
                Helper.sendMessage("> Vclipped " + distance + " blocks");
            } else {
                this.syntaxError(EnumChatFormatting.GRAY + args[0] + " is not a valid number");
            }
        } else {
            this.syntaxError(EnumChatFormatting.GRAY + "Valid .vclip <number>");
        }
        return null;
    }
}

