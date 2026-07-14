package ai.chat2db.community.jcef.event;

import ai.chat2db.community.jcef.listener.IEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class EventBus {
    private static final EventBus INSTANCE = new EventBus();
    private final Map<String, List<IEventListener>> listeners = new HashMap<>();

    private EventBus() {}

    public static EventBus getInstance() {
        return INSTANCE;
    }

    public void subscribe(String eventType, IEventListener listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()).add(listener);
    }

    public void unsubscribe(String eventType, IEventListener listener) {
        listeners.getOrDefault(eventType, new ArrayList<>()).remove(listener);
    }

    public void publish(IEvent event) {
        List<IEventListener> eventListeners = listeners.get(event.getType());
        if (eventListeners != null) {
            eventListeners.forEach(listener -> listener.onEvent(event));
        }
    }
}

