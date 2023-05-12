package nettion.features.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import nettion.features.command.Command;

public class Say extends Command {
    public Say() {
        super("Say", new String[]{}, "", "");
    }

    @Override
    public String execute(String[] args) throws Error {
        if (args.length >= 1) {
            String message = "";
            for (String str: args) {
                message += str + " ";
            }
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(message));
        }
        return null;
    }
}
