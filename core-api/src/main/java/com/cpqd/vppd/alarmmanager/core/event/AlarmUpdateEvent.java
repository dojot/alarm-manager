package com.cpqd.vppd.alarmmanager.core.event;

import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmOccurrence;

/**
 * Class that describes the event that is fired when an alarm event is received.
 */
public class AlarmUpdateEvent {
    private AlarmOccurrence event;
    private Alarm alarm;

    public AlarmUpdateEvent(final AlarmOccurrence event, final Alarm alarm) {
        this.event = event;
        this.alarm = alarm;
    }

    public AlarmOccurrence getEvent() {
        return event;
    }

    public void setEvent(AlarmOccurrence event) {
        this.event = event;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }
}
