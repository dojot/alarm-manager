package com.cpqd.vppd.alarmmanager.utils.exception;

/**
 * Created by fabio on 10/12/14.
 */
public class InvalidAlarmMetaModelXmlException extends Exception {

    private static final long serialVersionUID = -6745039357911022011L;

    /**
     * Constructor that receives a {@link Throwable}.
     *
     * @param t
     *            {@link Throwable} used to build exception
     */
    public InvalidAlarmMetaModelXmlException(final Throwable t) {
        super(t);
    }

    /**
     * Constructor that receives a message.
     *
     * @param message
     *            used to build exception
     */
    public InvalidAlarmMetaModelXmlException(final String message) {
        super(message);
    }
}
