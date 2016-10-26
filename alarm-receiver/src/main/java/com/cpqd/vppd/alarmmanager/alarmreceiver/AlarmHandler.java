package com.cpqd.vppd.alarmmanager.alarmreceiver;

import com.cpqd.vppd.alarmmanager.core.event.AlarmUpdateEvent;
import com.cpqd.vppd.alarmmanager.core.event.WBAlarmUpdateEvent;
import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmException;
import com.cpqd.vppd.alarmmanager.core.exception.UnknownAlarmMetaModelException;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModel;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModelManager;
import com.cpqd.vppd.alarmmanager.core.model.*;
import com.cpqd.vppd.alarmmanager.core.services.AlarmServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.List;

/**
 * Class that implements the logic of alarm handling.
 */
@Stateless
public class AlarmHandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmEventProcessor.class);

    @Inject
    private AlarmMetaModelManager alarmMetaModelManager;

    @Inject
    private AlarmServices alarmServices;

    @Inject
    private Validator alarmValidator;

    @Inject
    @WBAlarmUpdateEvent
    Event<AlarmUpdateEvent> alarmEventDispatcher;

    public void handleAlarmEvent(AlarmEvent alarmEvent) {

        // aux variables
        AlarmSeverity eventSeverity = alarmEvent.getSeverity();
        AlarmSeverity previousSeverity = null;
        AlarmOccurrence occurrence;
        Alarm persistedAlarm;

        if (alarmEvent.getClear_all()) {
            LOGGER.info("Let's Clear All!");

            List<Alarm> existingAlarms = alarmServices.findAllByPrimarySubject(alarmEvent);

            for (Alarm alarm : existingAlarms) {
                // update the existing alarm with the data from the event and save it
                alarmEvent.setSeverity(AlarmSeverity.Clear);
                alarm.updateWithEvent(alarmEvent);
                alarmServices.update(alarm);

                // fire an event indicating there's been an alarm update so interested parties are notified
                alarmEventDispatcher.fire(new AlarmUpdateEvent(AlarmOccurrence.Disappearance, null, alarm));
            }
            return;
        }

        try {
            validateAlarm(alarmEvent);
            LOGGER.info("validateAlarm");
        } catch (InvalidAlarmException e) {
            LOGGER.error("Received alarm event is invalid and will be discarded.");
            return;
        }

        // check if there is already a current alarm with the received primary key
        Alarm existingAlarm = alarmServices.find(alarmEvent);

        if (existingAlarm == null) {
            if (!AlarmSeverity.Clear.equals(eventSeverity)) {
                // the alarm does not exist in the system, add it
                occurrence = AlarmOccurrence.Appearance;
                persistedAlarm = Alarm.newFromAlarmEvent(alarmEvent);

                LOGGER.debug("Received event will be persisted as a new alarm");
                alarmServices.add(persistedAlarm);
            } else {
                LOGGER.warn("Received disappearance event for an unknown alarm. Event discarded");
                return;
            }
        } else {
            if (AlarmSeverity.Warning.equals(existingAlarm.getSeverity())) {
                // cannot change a persisted warning alarm
                LOGGER.warn("Unsupported update event for a warning alarm. Event discarded");
                return;
            }

            if (AlarmSeverity.Warning.equals(eventSeverity)) {
                // cannot change an existing alarm to warning severity
                LOGGER.warn("Received event attempted an illegal severity update. Event discarded");
                return;
            }

            if (AlarmSeverity.Clear.equals(eventSeverity)) {
                occurrence = AlarmOccurrence.Disappearance;
            } else {
                occurrence = AlarmOccurrence.Update;
                previousSeverity = existingAlarm.getSeverity();
            }

            // update the existing alarm with the data from the event and save it
            existingAlarm.updateWithEvent(alarmEvent);
            alarmServices.update(existingAlarm);

            LOGGER.debug("Received event is {} for an existing alarm",
                    AlarmOccurrence.Update.equals(occurrence) ? "an update" : "a disappearance");

            persistedAlarm = existingAlarm;
        }

        // fire an event indicating there's been an alarm update so interested parties are notified
        alarmEventDispatcher.fire(new AlarmUpdateEvent(occurrence, previousSeverity, persistedAlarm));
    }

    private void validateAlarm(AlarmEvent alarmEvent) throws InvalidAlarmException {
        // perform basic validation of mandatory fields
        Set<ConstraintViolation<AlarmEvent>> violations = alarmValidator.validate(alarmEvent);
        if (violations.size() > 0) {
            ConstraintViolation<AlarmEvent> first = violations.iterator().next();
            LOGGER.error("Invalid alarm event received: {} {}",
                    first.getPropertyPath().toString(), first.getMessageTemplate());
            throw InvalidAlarmException.buildFromNullField(first.getPropertyPath().toString());
        }

        // obtain the metamodel instance specified by the alarm instance
        AlarmMetaModel metaModel;
        try {
            metaModel = alarmMetaModelManager.getMetaModelForNamespaceAndDomain(alarmEvent.getNamespace(),
                    alarmEvent.getDomain());
        } catch (UnknownAlarmMetaModelException e) {
            LOGGER.error("Received alarm event mentions an unknown domain '{}'.", alarmEvent.getDomain());
            throw new InvalidAlarmException(InvalidAlarmException.Cause.UNKNOWN_DOMAIN);
        }

        // validate domain specific fields against the metamodel
        if (!validateDomainSpecificFieldsAgainstMetaModel(metaModel.getPrimarySubject(),
                alarmEvent.getPrimarySubject())) {
            LOGGER.error("Received alarm event's primary subject does not match specified meta model.");
            throw new InvalidAlarmException(InvalidAlarmException.Cause.MALFORMED_PRIMARY_SUBJECT);
        }

        if (alarmEvent.getAdditionalData() != null) {
            // additional data is optional
            if (!validateDomainSpecificFieldsAgainstMetaModel(metaModel.getAdditionalData(),
                    alarmEvent.getAdditionalData())) {
                LOGGER.error("Received alarm event's additional data does not match specified meta model.");
                throw new InvalidAlarmException(InvalidAlarmException.Cause.MALFORMED_ADDITIONAL_DATA);
            }
        }
    }

    private boolean validateDomainSpecificFieldsAgainstMetaModel(Set<DomainSpecificField> metaModelFields,
                                                                 Map<String, Object> eventFields) {
        // TODO validate field types
        if (metaModelFields.size() != eventFields.size()) {
            return false;
        }

        for (DomainSpecificField metaModelField : metaModelFields) {
            if (!eventFields.containsKey(metaModelField.getName())) {
                return false;
            }
        }

        return true;
    }
}
