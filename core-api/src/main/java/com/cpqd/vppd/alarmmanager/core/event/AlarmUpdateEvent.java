package com.cpqd.vppd.alarmmanager.core.event;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmOccurrence;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;

/**
 * Class that describes the event that is fired when an alarm event is received.
 */
public class AlarmUpdateEvent {
    private AlarmOccurrence event;
    private AlarmSeverity previousSeverity;
    private Alarm alarm;

    public AlarmUpdateEvent(final AlarmOccurrence event,
                            final AlarmSeverity previousSeverity,
                            final Alarm alarm) {
        this.event = event;
        this.previousSeverity = previousSeverity;
        this.alarm = alarm;
    }

    public AlarmOccurrence getEvent() {
        return event;
    }

    public void setEvent(AlarmOccurrence event) {
        this.event = event;
    }

    public AlarmSeverity getPreviousSeverity() {
        return previousSeverity;
    }

    public void setPreviousSeverity(AlarmSeverity previousSeverity) {
        this.previousSeverity = previousSeverity;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }
}
