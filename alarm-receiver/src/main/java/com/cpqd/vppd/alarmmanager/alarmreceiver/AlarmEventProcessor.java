package com.cpqd.vppd.alarmmanager.alarmreceiver;

import com.cpqd.vppd.alarmmanager.core.model.AlarmEvent;
import com.cpqd.vppd.alarmmanager.utils.AlarmJsonConverter;
import com.cpqd.vppd.alarmmanager.utils.exception.InvalidAlarmJsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Asynchronously handles the received alarms.
 *
 * @author Fabio Margarido
 */
@Stateless
public class AlarmEventProcessor {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmEventProcessor.class);

    @Inject
    private AlarmJsonConverter jsonConverter;

    @Inject
    private AlarmHandler alarmHandler;

    /**
     * Responsible for handling the incoming events.
     *
     * @param alarmEventJson To be handled.
     */
    @Asynchronous
    public void onAlarmReceived(final String alarmEventJson) {
        LOGGER.debug("Alarm event received: " + alarmEventJson);

        // convert the received JSON into an AlarmEvent instance
        AlarmEvent alarmEvent;
        try {
            alarmEvent = jsonConverter.fromEventJson(alarmEventJson);
        } catch (InvalidAlarmJsonException e) {
            LOGGER.error("Error converting received JSON into an Alarm instance", e);
            return;
        }

        // handle the received alarm event
        alarmHandler.handleAlarmEvent(alarmEvent);
    }
}
