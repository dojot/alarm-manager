package com.cpqd.vppd.alarmmanager.core.converter;

import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmJsonException;
import com.cpqd.vppd.alarmmanager.core.model.AlarmEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;

/**
 * Utility methods for converting an Alarm instance to and from JSON strings.
 */
@ApplicationScoped
public class AlarmJsonConverter {
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    static {
        jsonMapper.registerModule(new JodaModule());
    }

    public String toJson(Object object) throws InvalidAlarmJsonException {
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new InvalidAlarmJsonException(e);
        }
    }

    public AlarmEvent fromEventJson(String alarmEventJson) throws InvalidAlarmJsonException {
        try {
            return jsonMapper.readValue(alarmEventJson, AlarmEvent.class);
        } catch (IOException e) {
            throw new InvalidAlarmJsonException(e);
        }
    }
}
