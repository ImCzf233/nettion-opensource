package nettion.event.events.misc;

import net.minecraft.util.IChatComponent;
import nettion.event.Event;

public class EventChatReceived extends Event {

    /**
     * Introduced in 1.8:
     * 0 : Standard Text Message
     * 1 : 'System' message, displayed as standard text.
     * 2 : 'Status' message, displayed above action bar, where song notifications are.
     */
    public final byte type;
    public IChatComponent message;
    private final String rawMessage;

    public EventChatReceived(byte type, IChatComponent message) {
        this.type = type;
        this.message = message;
        this.rawMessage = message.getUnformattedText();
    }

    public String getRawMessage() {
        return rawMessage;
    }
}
