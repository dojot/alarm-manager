package com.cpqd.vppd.alarmmanager.signaling;

import com.cpqd.vppd.alarmmanager.core.converter.AlarmJsonConverter;
import com.cpqd.vppd.alarmmanager.core.event.AlarmUpdateEvent;
import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmJsonException;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class for alarm updates signaling through a WebSocket.
 */
@Singleton
@ServerEndpoint("/currentupdates")
public class AlarmWebSocketSignaling {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmWebSocketSignaling.class);

    @Inject
    private AlarmJsonConverter alarmJsonConverter;

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
    public void onOpen(Session session) {
        LOGGER.info("WebSocket session opened: {}", session.getId());
        connectedPeers.add(session);
    }

    /**
     * Method called by the container
     * @param alarm
     */
    public void onAlarmUpdateEvent(@Observes @AlarmUpdateEvent Alarm alarm) {
        if (!connectedPeers.isEmpty()) {
            LOGGER.info("Alarm update event observed. Notifying connected peers");

            String updateJson;
            try {
                updateJson = alarmJsonConverter.toJson(alarm);
            } catch (InvalidAlarmJsonException e) {
                LOGGER.error("Unable to convert alarm update event to JSON. Listeners will not be notified.");
                return;
            }

            for (Session peer : connectedPeers) {
                peer.getAsyncRemote().sendText(updateJson);
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
        LOGGER.info("WebSocket session closed: {}", session.getId());
        connectedPeers.remove(session);
    }
}
