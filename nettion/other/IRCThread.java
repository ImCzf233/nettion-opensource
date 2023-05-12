package nettion.other;

import nettion.Nettion;
import nettion.features.module.modules.player.IRC;
import nettion.utils.Helper;

public class IRCThread extends Thread {

    @Override
    public void run(){
        IRC.connect();
        while(true){
            IRC.handleInput();
            if(!Nettion.instance.getModuleManager().getModuleByClass(IRC.class).isEnabled()){
                Helper.sendMessage("��4IRC�Ͽ�");
                break;
            }
        }
    }
}
