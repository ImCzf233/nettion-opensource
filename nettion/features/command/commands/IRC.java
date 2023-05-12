package nettion.features.command.commands;

import net.minecraft.util.EnumChatFormatting;
import nettion.features.command.Command;
import nettion.utils.Helper;

public class IRC extends Command {
    public static String username = null;

    public IRC(){
        super("IRC",new String[]{"list"}, "", "sketit");
    }


    @Override
    public String execute(String[] args) {
        if(args.length == 1) {
            nettion.features.module.modules.player.IRC.sendMessage("MSG@"+ Helper.mc.thePlayer.getName() + "@" + EnumChatFormatting.WHITE + "User" + "@" + args[0]);
        }
        return null;
    }
}
