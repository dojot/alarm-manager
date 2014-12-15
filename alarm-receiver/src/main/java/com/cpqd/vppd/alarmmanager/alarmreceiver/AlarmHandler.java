package com.cpqd.vppd.alarmmanager.alarmreceiver;

import com.cpqd.vppd.alarmmanager.core.exception.AlarmNotPresentException;
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
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
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
        // validate alarm instance against metamodel
        Set<ConstraintViolation<AlarmEvent>> violations = alarmValidator.validate(alarmEvent);
        if (!violations.isEmpty()) {
            LOGGER.error("Invalid alarm event received: {}", violations);
            return;
        }

        AlarmMetaModel metaModel;
        try {
            metaModel = alarmMetaModelManager.getMetaModelForDomain(alarmEvent.getDomain());
        } catch (UnknownAlarmMetaModelException e) {
            LOGGER.error("Received alarm event mentions an unknown domain '{}'. Event discarded.", alarmEvent.getDomain());
            return;
        }

        if (!validateVariableFieldsAgainstMetaModel(metaModel.getPrimarySubject(), alarmEvent.getPrimarySubject())) {
            LOGGER.error("Received alarm event's primary subject does not match specified meta model. Event discarded");
            return;
        }

        if (!validateVariableFieldsAgainstMetaModel(metaModel.getAdditionalData(), alarmEvent.getAdditionalData())) {
            LOGGER.error("Received alarm event's additional data does not match specified meta model. Event discarded");
        }

        // handle alarm
        if (AlarmSeverity.Clear.equals(alarmEvent.getSeverity())) {
            // alarm disappearance
            try {
                alarmServices.clear(alarmEvent.getPrimarySubject(), alarmEvent.getEventTimestamp());
            } catch (AlarmNotPresentException e) {
                LOGGER.warn("Received disappearance event for an alarm that was not present");
            }
        } else {
            // alarm appearance or update
            // check if the alarm is already present
            boolean alarmExists = alarmServices.existsByPrimarySubject(alarmEvent.getPrimarySubject());
            Alarm receivedAlarm = Alarm.fromAlarmEvent(alarmEvent, !alarmExists);

            if (alarmExists) {
                alarmServices.update(receivedAlarm);
            } else {
                alarmServices.add(receivedAlarm);
            }
        }
    }

    private void validateAlarm(AlarmEvent alarmEvent) {
        // validate alarm instance against metamodel
        Set<ConstraintViolation<AlarmEvent>> violations = alarmValidator.validate(alarmEvent);
        if (!violations.isEmpty()) {
            LOGGER.error("Invalid alarm event received: {}", violations);
            return;
        }

        AlarmMetaModel metaModel;
        try {
            metaModel = alarmMetaModelManager.getMetaModelForDomain(alarmEvent.getDomain());
        } catch (UnknownAlarmMetaModelException e) {
            LOGGER.error("Received alarm event mentions an unknown domain '{}'. Event discarded.", alarmEvent.getDomain());
            return;
        }

        if (!validateVariableFieldsAgainstMetaModel(metaModel.getPrimarySubject(), alarmEvent.getPrimarySubject())) {
            LOGGER.error("Received alarm event's primary subject does not match specified meta model. Event discarded");
            return;
        }

        if (!validateVariableFieldsAgainstMetaModel(metaModel.getAdditionalData(), alarmEvent.getAdditionalData())) {
            LOGGER.error("Received alarm event's additional data does not match specified meta model. Event discarded");
        }
    }

    private boolean validateVariableFieldsAgainstMetaModel(Set<DomainSpecificField> metaModelFields,
                                                           Set<DomainSpecificField> eventFields) {
        // TODO validate field types
        return metaModelFields.equals(eventFields);
    }
}
