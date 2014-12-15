package com.cpqd.vppd.alarmmanager.core.exception;

/**
 * Created by fabio on 10/12/14.
 */
public class DuplicateAlarmMetaModelException extends Exception {

    private static final long serialVersionUID = 1791999561586033986L;

    public DuplicateAlarmMetaModelException(String message) {
        super(message);
    }

    public DuplicateAlarmMetaModelException(Throwable cause) {
        super(cause);
    }
}
