package contactus.event;

import java.util.*;

public class EventDispatcher {
    private final Map<Class, List<EventListener<?>>> listenersMap = new HashMap<>();

    public <T> void addListener(Class<T> type, EventListener<T> listener) {
        List<EventListener<?>> listeners = listenersMap.computeIfAbsent(type, k -> new ArrayList<>());
        listeners.add(listener);
    }

    public <T> void removeListener(Class<T> type, EventListener<T> listener) {
        List<EventListener<?>> listeners = listenersMap.getOrDefault(type, Collections.emptyList());
        listeners.remove(listener);
    }

    public <T> void dispatchEvent(T event) {
        List<EventListener<?>> listeners = listenersMap.getOrDefault(event.getClass(), Collections.emptyList());
        for (EventListener<?> listener : listeners) {
            ((EventListener<T>)listener).onEvent(event);
        }
    }

    public void removeAllListeners() {
        listenersMap.clear();
    }
}
