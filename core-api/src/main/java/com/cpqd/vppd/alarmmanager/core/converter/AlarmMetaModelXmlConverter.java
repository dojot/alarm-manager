package com.cpqd.vppd.alarmmanager.core.converter;

import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModel;
import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmMetaModelXmlException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;

/**
 * Utility methods for converting an alarm metamodel instance to and from XML.
 */
@ApplicationScoped
public class AlarmMetaModelXmlConverter {
    private static final ObjectMapper xmlMapper = new XmlMapper();

    public String toXml(AlarmMetaModel alarmMetaModel) throws InvalidAlarmMetaModelXmlException {
        try {
            return xmlMapper.writeValueAsString(alarmMetaModel);
        } catch (JsonProcessingException e) {
            throw new InvalidAlarmMetaModelXmlException(e);
        }
    }

    public AlarmMetaModel fromXml(String xml) throws InvalidAlarmMetaModelXmlException {
        try {
            return xmlMapper.readValue(xml, AlarmMetaModel.class);
        } catch (IOException e) {
            throw new InvalidAlarmMetaModelXmlException(e);
        }
    }
}
