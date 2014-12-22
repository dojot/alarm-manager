package com.cpqd.vppd.alarmmanager.alarmreceiver;

import com.cpqd.vppd.alarmmanager.core.event.AlarmUpdateEvent;
import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmException;
import com.cpqd.vppd.alarmmanager.core.exception.UnknownAlarmMetaModelException;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModel;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModelManager;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmEvent;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.model.DomainSpecificField;
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
    @AlarmUpdateEvent
    Event<Alarm> alarmEventDispatcher;

    public void handleAlarmEvent(AlarmEvent alarmEvent) {
        try {
            validateAlarm(alarmEvent);
        } catch (InvalidAlarmException e) {
            LOGGER.error("Received alarm event is invalid and will be discarded.");
            return;
        }

        Alarm existingAlarm = alarmServices.findCurrentByDomainAndPrimarySubject(alarmEvent.getDomain(),
                alarmEvent.getPrimarySubject());
        Alarm persistedAlarm;

        if (existingAlarm == null) {
            // the alarm does not exist in the system, add it
            persistedAlarm = Alarm.newFromAlarmEvent(alarmEvent);

            LOGGER.debug("Received event will be persisted as a new alarm");
            alarmServices.add(persistedAlarm);
        } else {
            // update the existing alarm with the data from the event
            existingAlarm.updateWithEvent(alarmEvent);
            persistedAlarm = existingAlarm;

            LOGGER.debug("Received event is an update for an existing alarm");
            alarmServices.update(persistedAlarm);
        }

        // fire an event indicating there's been an alarm update so interested parties are notified
        alarmEventDispatcher.fire(persistedAlarm);
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
            metaModel = alarmMetaModelManager.getMetaModelForDomain(alarmEvent.getDomain());
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
