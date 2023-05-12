package nettion.features.module.modules.movement;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import nettion.event.EventHandler;
import nettion.event.events.world.EventPacketSend;
import nettion.event.events.world.EventUpdate;
import nettion.features.module.Module;
import nettion.features.module.ModuleManager;
import nettion.features.module.ModuleType;
import nettion.features.value.values.Mode;
import nettion.features.value.values.Option;

public class Sprint
extends Module {
    public final Mode<Enum> allDirectionsBypassValue = new Mode<>("OmniMode", allDirectionsBypassMode.values(), allDirectionsBypassMode.None);
    public final Option<Boolean> noPacketValue = new Option<>("NoPackets", false);
    public Sprint() {
        super("Sprint", ModuleType.Movement);
        addValues(allDirectionsBypassValue, noPacketValue);
    }

    boolean switchStat = false;

    @EventHandler
    private void onUpdate(EventUpdate event) {
        if (ModuleManager.getModuleByClass(Scaffold.class).isEnabled() && (Scaffold.sprintMode.getValue() != Scaffold.spmod.Normal)) {
            return;
        }
        if (!mc.thePlayer.isMoving()) {
            return;
        }
        if (mc.gameSettings.keyBindForward.isKeyDown()) {
            if ((!ModuleManager.getModuleByClass(NoSlow.class).isEnabled() && mc.thePlayer.isUsingItem())) {
                return;
            }
            mc.thePlayer.setSprinting(true);
        }
        if (allDirectionsBypassValue.getValue() != allDirectionsBypassMode.None) {
            if (allDirectionsBypassValue.getValue() == allDirectionsBypassMode.SpamSprint) {
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            } else if (allDirectionsBypassValue.getValue() == allDirectionsBypassMode.Spoof) {
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                switchStat = true;
            } else if (allDirectionsBypassValue.getValue() == allDirectionsBypassMode.Normal) {
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
            }
        } else {
            switchStat = false;
        }
    }


    @EventHandler
    private void onPacketSend(EventPacketSend event) {
        if (ModuleManager.getModuleByClass(Scaffold.class).isEnabled() && Scaffold.sprintMode.getValue() != Scaffold.spmod.Normal) {
            return;
        }
        if (!mc.thePlayer.isMoving()) {
            return;
        }
        Packet<?> packet = event.packet;
        if (packet instanceof C0BPacketEntityAction) {
            if (allDirectionsBypassValue.getValue() != allDirectionsBypassMode.None) {
                if (allDirectionsBypassValue.getValue() == allDirectionsBypassMode.SpamSprint) {
                    if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                        event.cancel();
                    }
                } else if (allDirectionsBypassValue.getValue() == allDirectionsBypassMode.NoStopSprint) {
                    if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                        event.cancel();
                    }
                } else if (allDirectionsBypassValue.getValue() == allDirectionsBypassMode.Spoof) {
                    if (switchStat) {
                        if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING || ((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                            event.cancel();
                        }
                    }
                } else if (allDirectionsBypassValue.getValue() == allDirectionsBypassMode.Normal) {
                    if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                        event.cancel();
                    }
                }
            }
            if (noPacketValue.getValue() && !event.isCancelled()) {
                if (((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.STOP_SPRINTING || ((C0BPacketEntityAction) packet).getAction() == C0BPacketEntityAction.Action.START_SPRINTING) {
                    event.cancel();
                }
            }
        }
    }

    enum allDirectionsBypassMode {
        Normal,
        Spoof,
        SpamSprint,
        NoStopSprint,
        None,
    }
}
