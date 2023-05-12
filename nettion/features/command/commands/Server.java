package nettion.features.command.commands;

import nettion.utils.Helper;
import net.minecraft.client.Minecraft;
import nettion.features.command.Command;

public class Server extends Command {
    public Server() {
        super("Server", new String[]{"serverinfo"}, "", "server");
    }

    @Override
    public String execute(String[] args) {
        if (!Minecraft.getMinecraft().isSingleplayer()) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("Server IP: " + Minecraft.getMinecraft().getCurrentServerData().serverIP);
            Minecraft.getMinecraft().thePlayer.sendChatMessage("Server Version: " + Minecraft.getMinecraft().getCurrentServerData().gameVersion);
        } else {
            Helper.sendMessage("Multiplayer only!");
        }
        return null;
    }
}
