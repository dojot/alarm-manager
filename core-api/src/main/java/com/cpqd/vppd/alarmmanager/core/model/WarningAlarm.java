package com.cpqd.vppd.alarmmanager.core.model;

import javax.persistence.Entity;

/**
 * Created by fabio on 05/12/14.
 */
@Entity
public class WarningAlarm extends Alarm {
    public WarningAlarm() {

    }

    public WarningAlarm(BasicAlarmData alarm) {
        super(alarm);
    }
}
