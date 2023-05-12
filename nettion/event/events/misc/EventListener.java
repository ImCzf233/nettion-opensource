package nettion.event.events.misc;

public interface EventListener<T> {
    void call(T event);
}
