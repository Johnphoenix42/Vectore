package appcustomcontrol;

import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.input.InputEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * This class can be implemented by any class that, through EventType(s), is expected to be capable of
 * drawing something on appcomponent.DrawPane.
 * @param <T> an Event class that encapsulates all Event types it requires from appcomponent.DrawPane to grant. It's
 *           usually either InputEvent or MouseEvent
 */
public interface DrawTriggerable {

    <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> draw(EventType<T> eventType, T ev);

    <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> unDraw(EventType<T> eventType, T ev);

}
