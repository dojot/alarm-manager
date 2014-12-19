package com.cpqd.vppd.alarmmanager.converter

import com.cpqd.vppd.alarmmanager.core.converter.AlarmJsonConverter
import com.cpqd.vppd.alarmmanager.core.model.AlarmEvent
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity
import com.cpqd.vppd.alarmmanager.core.model.DomainSpecificField
import org.apache.commons.io.FileUtils
import spock.lang.Specification

/**
 * Created by fabio on 05/12/14.
 */
class TestAlarmJsonConverter extends Specification {

    private AlarmJsonConverter alarmJsonConverter

    def setup() {
        alarmJsonConverter = new AlarmJsonConverter()
    }

    def "convert a warning alarm from JSON"() {
        given: "there is a JSON which represents a warning alarm"
        String json = FileUtils.readFileToString(new File("src/test/resources/json/warning-alarm.json"))

        when: "the JSON string is converted to an alarm event"
        AlarmEvent alarmEvent = alarmJsonConverter.fromEventJson(json)

        then: "an alarm event should be instantiated with the following properties"
        alarmEvent != null
        alarmEvent.domain == "ROADMInterfaceAlarm"
        alarmEvent.description == "Interface Exploded"
        alarmEvent.severity == AlarmSeverity.Warning
        alarmEvent.primarySubject.size() == 2
        alarmEvent.primarySubject.containsKey("nodeAlias")
        alarmEvent.primarySubject.containsKey("interfaceAlias")
        alarmEvent.additionalData.size() == 1
        alarmEvent.additionalData.containsKey("auxField1")
        alarmEvent.eventTimestamp != null
    }
}
