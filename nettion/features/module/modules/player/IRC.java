package nettion.features.module.modules.player;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import nettion.Nettion;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.other.IRCThread;
import nettion.utils.Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class IRC extends Module {
    public BufferedReader reader;
    public static Socket socket;
    public static PrintWriter pw;
    static InputStream in;

    public IRC() {
        super("IRC", ModuleType.Player);
    }

    @Override
    public void onDisable(){
        sendMessage("CLOSE");
        Helper.sendMessage("你退出了IRC");
    }

    @Override
    public void onEnable(){
        new IRCThread().start();
    }

    public static void handleInput() {
        byte[] data = new byte[1024];
        try {
            int len=in.read(data);
            String ircmessage = new String(data,0,len);
            ircmessage = ircmessage.replaceAll("\n","");
            ircmessage = ircmessage.replaceAll("\r","");
            ircmessage = ircmessage.replaceAll("\t","");
            if (ircmessage.equals("CLOSE")){
                Helper.sendMessage("§4IRC服务器关闭");
                Nettion.instance.getModuleManager().getModuleByClass(IRC.class).setEnabled(false);
                return;
            } else if(ircmessage.equals("你被踢出了IRC")){
                System.exit(99);
            }
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(ircmessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void connect(){
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("尝试连接服务器"));
        try {
            socket = new Socket("127.0.0.1", 12754);
            in=socket.getInputStream();
            pw = new PrintWriter(socket.getOutputStream(), true);
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("连接成功"));
            pw.println(mc.thePlayer.getName() + "@" + " " + "@Nettion@" + Minecraft.getMinecraft().thePlayer.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void sendMessage(String msg){
        pw.println(msg);
    }
}
