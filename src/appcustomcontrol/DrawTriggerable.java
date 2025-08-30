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

    /**
     * Implement this method if you want your class to be able to draw on the canvas. What is to be drawn is specified
     * in renderTree, a map object that will be created in the method locally
     * @param eventType
     * @param ev
     * @return
     * @param <T>
     */
    <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> draw(EventType<T> eventType, T ev);

    /**
     * Implement this method if you want your class to be able to unDraw (clear a node) on the canvas.
     * What is to be unDrawn is specified in renderTree, a map object that will be created in the method locally
     * @param eventType
     * @param ev
     * @return
     * @param <T>
     */
    <T extends InputEvent> Map<String, LinkedHashMap<String, Node>> unDraw(EventType<T> eventType, T ev);

}
