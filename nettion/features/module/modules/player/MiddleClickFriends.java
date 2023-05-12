/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package nettion.features.module.modules.player;

import nettion.event.EventHandler;
import nettion.event.events.world.EventPreUpdate;
import nettion.other.FriendManager;
import nettion.features.module.Module;
import nettion.features.module.ModuleType;

import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

public class MiddleClickFriends
extends Module {
    private boolean down;

    public MiddleClickFriends() {
        super("MCF", ModuleType.Player);
    }

    @EventHandler
    private void onClick(EventPreUpdate e) {
        if (Mouse.isButtonDown(2) && !this.down) {
            if (mc.objectMouseOver.entityHit != null) {
                EntityPlayer player = (EntityPlayer)this.mc.objectMouseOver.entityHit;
                String playername = player.getName();
                if (!FriendManager.isFriend(playername)) {
                    mc.thePlayer.sendChatMessage(".f add " + playername);
                } else {
                    mc.thePlayer.sendChatMessage(".f del " + playername);
                }
            }
            this.down = true;
        }
        if (!Mouse.isButtonDown(2)) {
            this.down = false;
        }
    }
}

