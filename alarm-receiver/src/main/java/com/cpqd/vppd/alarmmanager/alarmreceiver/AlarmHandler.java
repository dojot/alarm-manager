package com.cpqd.vppd.alarmmanager.alarmreceiver;

import com.cpqd.vppd.alarmmanager.core.exception.AlarmNotPresentException;
import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmException;
import com.cpqd.vppd.alarmmanager.core.exception.UnknownAlarmMetaModelException;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModel;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModelManager;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmEvent;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.model.DomainSpecificField;
import com.cpqd.vppd.alarmmanager.core.services.AlarmServices;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by fabio on 05/12/14.
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

    public void handleAlarmEvent(AlarmEvent alarmEvent) {
        try {
            validateAlarm(alarmEvent);
        } catch (InvalidAlarmException e) {
            LOGGER.error("Received alarm event is invalid and will be discarded.");
            return;
        }

        // handle alarm
        if (AlarmSeverity.Clear.equals(alarmEvent.getSeverity())) {
            // TODO alarm disappearance
            try {
                alarmServices.clear(alarmEvent.getPrimarySubject(), alarmEvent.getEventTimestamp());
            } catch (AlarmNotPresentException e) {
                LOGGER.warn("Received disappearance event for an alarm that was not present");
            }
        } else {
            // alarm appearance or update
            // check if the alarm is already present
            Alarm existingAlarm = alarmServices.findByPrimarySubject(alarmEvent.getPrimarySubject());
            Alarm receivedAlarm = Alarm.fromAlarmEvent(alarmEvent, existingAlarm != null);

            if (existingAlarm != null) {
                LOGGER.debug("Received event is an update for an existing alarm");
                alarmServices.update(receivedAlarm);
            } else {
                // the alarm does not exist in the system, add it
                LOGGER.debug("Received event will be persisted as a new alarm");
                alarmServices.add(receivedAlarm);
            }
        }
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

        if (!validateDomainSpecificFieldsAgainstMetaModel(metaModel.getAdditionalData(),
                alarmEvent.getAdditionalData())) {
            LOGGER.error("Received alarm event's additional data does not match specified meta model.");
            throw new InvalidAlarmException(InvalidAlarmException.Cause.MALFORMED_ADDITIONAL_DATA);
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
