package ca.riveros.ib.events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Created by admin on 11/28/16.
 */
public class EventTypes {

    public static EventType<Event> twsEndStreamEventType = new EventType<>(Event.ANY, "TWS_END_STREAM");
    public static EventType<Event> netLiqEventType = new EventType<>(Event.ANY, "NET_LIQ_EVENT_TYPE");

}
