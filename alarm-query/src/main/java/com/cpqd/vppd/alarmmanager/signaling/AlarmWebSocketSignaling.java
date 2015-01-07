package com.cpqd.vppd.alarmmanager.signaling;

import com.cpqd.vppd.alarmmanager.core.converter.AlarmJsonConverter;
import com.cpqd.vppd.alarmmanager.core.event.AlarmUpdateEvent;
import com.cpqd.vppd.alarmmanager.core.event.WBAlarmUpdateEvent;
import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmJsonException;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.services.AlarmMetadataServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class for alarm updates signaling through a WebSocket.
 */
@Singleton
@ServerEndpoint("/currentupdates/{namespace}")
public class AlarmWebSocketSignaling {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmWebSocketSignaling.class);

    @Inject
    private AlarmJsonConverter alarmJsonConverter;

    @Inject
    private AlarmMetadataServices alarmMetadataServices;

    /**
     * Set that holds all the connected sessions.
     */
    private static Set<Session> connectedPeers = Collections.synchronizedSet(new HashSet<Session>());

    /**
     * Method called by the container when a new WebSocket session is opened.
     * We store the session in the collection of opened sessions so peers can be notified of
     * alarm updates.
     *
     * @param session the opened session.
     */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("namespace") String namespace) {
        LOGGER.info("[{}] WebSocket session opened for namespace {}", session.getId(), namespace);
        // store the association of this peer with the specified namespace
        session.getUserProperties().put(namespace, true);
        connectedPeers.add(session);
    }

    /**
     * Method called by the container when an alarm update event is raised.
     * @param alarmUpdateEvent the raised event.
     */
    public void onAlarmUpdateEvent(@Observes @WBAlarmUpdateEvent AlarmUpdateEvent alarmUpdateEvent) {
        // FIXME if the priority of the observers is undefined, we may obtain
        // FIXME information that is not up to date
        if (!connectedPeers.isEmpty()) {
            LOGGER.info("Alarm update event observed. Notifying connected peers");

            // the JSON event will be lazily generated
            String updateJson = null;

            for (Session session : connectedPeers) {
                if (Boolean.TRUE.equals(session.getUserProperties().get(alarmUpdateEvent.getAlarm().getNamespace()))) {
                    if (updateJson == null) {
                        Map<AlarmSeverity, AtomicLong> metadata =
                                alarmMetadataServices.getCurrentAlarmsMetadata(alarmUpdateEvent.getAlarm().getNamespace());
                        Map<String, Object> notification = new HashMap<>();

                        notification.put("event", alarmUpdateEvent.getEvent());
                        notification.put("alarm", alarmUpdateEvent.getAlarm());
                        notification.put("metadata", metadata);

                        try {
                            updateJson = alarmJsonConverter.toJson(notification);
                        } catch (InvalidAlarmJsonException e) {
                            LOGGER.error("Unable to convert alarm update event to JSON. Listeners will not be notified.");
                            return;
                        }
                    }

                    // send the notification to the peer
                    session.getAsyncRemote().sendText(updateJson);
                }
            }
        }
    }

    /**
     * Called by the container when a peer closes a WebSocket session.
     * The closed session is removed from the collection.
     *
     * @param session the closed session.
     * @param reason the reason.
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        LOGGER.info("[{}] WebSocket session closed", session.getId());
        connectedPeers.remove(session);
    }
}
