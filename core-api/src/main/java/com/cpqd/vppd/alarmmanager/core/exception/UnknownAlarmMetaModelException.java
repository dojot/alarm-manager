package com.cpqd.vppd.alarmmanager.core.exception;

/**
 * Created by fabio on 11/12/14.
 */
public class UnknownAlarmMetaModelException extends Exception {
    private static final long serialVersionUID = 5335742525098581791L;

    public UnknownAlarmMetaModelException(Throwable cause) {
        super(cause);
    }

    public UnknownAlarmMetaModelException(String message) {
        super(message);
    }
}
