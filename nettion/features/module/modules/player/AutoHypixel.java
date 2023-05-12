package nettion.features.module.modules.player;

import net.minecraft.item.Item;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S02PacketChat;
import nettion.event.EventHandler;
import nettion.event.events.world.*;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Numbers;
import nettion.features.value.values.Option;
import nettion.ui.notification.NotificationManager;
import nettion.ui.notification.NotificationType;
import nettion.utils.ServerUtils;
import nettion.utils.time.TimerUtils;

public class AutoHypixel extends Module {
    private final Option<Boolean> gg = new Option<>("AutoGG", true);
    private final Option<Boolean> play = new Option<>("AutoPlay", true);
    private final Numbers<Double> ahdelay = new Numbers<>("Delay", 3.0, 0.0, 5.0, 0.1);

    private final TimerUtils timer = new TimerUtils();
    public String playCommand = "";
    private boolean autoplay = false, autogg = false, loaded = false;

    public AutoHypixel() {
        super("AutoHypixel", ModuleType.Player);
        addValues(gg, play, ahdelay);
    }

    @Override
    public void onEnable() {
        this.autoplay = false;
        this.autogg = false;
        timer.reset();
        super.onEnable();
    }

    @EventHandler
    public void onUpdate(EventUpdate event) {
        int delay = ahdelay.getValue().intValue();
        if(ServerUtils.isHypixel()) {
            if(gg.getValue()) {
                if(this.autogg == true) {
                    mc.thePlayer.sendChatMessage("/achat GG");
                    this.autogg = false;
                }
            }

            if(play.getValue()) {
                if(this.autoplay == true) {
                    if(!loaded) {
                        NotificationManager.post(NotificationType.INFO, "Info", "Sending you to you the next game in (" + delay + "s)", delay);
                        loaded = true;
                    }
                    if(timer.delay(1000 * delay)) {
                        mc.thePlayer.sendChatMessage(playCommand);
                        timer.reset();
                        this.autoplay = false;
                        loaded = false;
                    }
                }else {
                    timer.reset();
                }
            }
        }
    }

    @EventHandler
    public void onLoadWorld(EventWorldLoad event) {
        this.autoplay = false;
        this.autogg = false;
        timer.reset();
    }

    @EventHandler
    public void onReceivePacket(EventPacketReceive event) {

        if(ServerUtils.isHypixel()) {
            if (event.getPacket() instanceof S02PacketChat) {
                S02PacketChat chatPacket = (S02PacketChat) event.getPacket();
                String chatMessage = chatPacket.getChatComponent().getUnformattedText();
                if(chatMessage.contains("WINNER!") ||  chatMessage.contains("1st Killer -") || chatMessage.contains("Top Survivors")) {
                    this.autogg = true;
                }

                if(chatMessage.contains("WINNER!") ||  chatMessage.contains("1st Killer -") || chatMessage.contains("Top Survivors") || chatMessage.contains("You died!")) {
                    this.autoplay = true;
                }
            }
        }
    }

    @EventHandler
    public void onSendPacket(EventPacketSend e) {
        if (ServerUtils.isHypixel()) {
            if (playCommand.startsWith("/play ")) {
                String display = playCommand.replace("/play ", "").replace("_", " ");
                boolean nextUp = true;
                for (char c : display.toCharArray()) {
                    if (c == ' ') {
                        nextUp = true;
                        continue;
                    }
                    if (nextUp) {
                        nextUp = false;
                    }
                }
            }

            if (e.getPacket() instanceof C0EPacketClickWindow) {

                C0EPacketClickWindow packet = (C0EPacketClickWindow) e.getPacket();
                String itemname;

                if(packet.getClickedItem() == null) {
                    return;
                }

                itemname = packet.getClickedItem().getDisplayName();

                if (packet.getClickedItem().getDisplayName().startsWith("\247a")) {
                    int itemID = Item.getIdFromItem(packet.getClickedItem().getItem());
                    if (itemID == 381 || itemID == 368) {
                        if (itemname.contains("SkyWars")) {
                            if (itemname.contains("Doubles")) {
                                if (itemname.contains("Normal")) {
                                    playCommand = "/play teams_normal";
                                } else if (itemname.contains("Insane")) {
                                    playCommand = "/play teams_insane";
                                }
                            } else if (itemname.contains("Solo")) {
                                if (itemname.contains("Normal")) {
                                    playCommand = "/play solo_normal";
                                } else if (itemname.contains("Insane")) {
                                    playCommand = "/play solo_insane";
                                }
                            }
                        }
                    } else if (itemID == 355) {
                        if (itemname.contains("Bed Wars")) {
                            if (itemname.contains("4v4")) {
                                playCommand = "/play bedwars_four_four";
                            } else if (itemname.contains("3v3")) {
                                playCommand = "/play bedwars_four_three";
                            } else if (itemname.contains("Doubles")) {
                                playCommand = "/play bedwars_eight_two";
                            } else if (itemname.contains("Solo")) {
                                playCommand = "/play bedwars_eight_one";
                            }
                        }
                    }
                }
            } else if (e.getPacket() instanceof C01PacketChatMessage) {
                C01PacketChatMessage packet = (C01PacketChatMessage) e.getPacket();
                if (packet.getMessage().startsWith("/play")) {
                    playCommand = packet.getMessage();
                }
            }
        }
    }
}
