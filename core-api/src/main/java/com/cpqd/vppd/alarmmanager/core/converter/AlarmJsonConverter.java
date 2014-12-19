package com.cpqd.vppd.alarmmanager.core.converter;

import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmJsonException;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;

/**
 * Utility methods for converting an Alarm instance to and from JSON strings.
 */
@ApplicationScoped
public class AlarmJsonConverter {
    private static final ObjectMapper jsonMapper = new ObjectMapper();

    static {
        jsonMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
    }

    public String toJson(Alarm alarm) throws InvalidAlarmJsonException {
        try {
            return jsonMapper.writeValueAsString(alarm);
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
