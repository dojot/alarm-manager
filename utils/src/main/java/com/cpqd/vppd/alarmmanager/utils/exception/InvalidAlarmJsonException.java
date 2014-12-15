package com.cpqd.vppd.alarmmanager.utils.exception;

/**
 * Created by fabio on 05/12/14.
 */
public class InvalidAlarmJsonException extends Exception {

    private static final long serialVersionUID = -7037899140554325229L;

    /**
     * Constructor that receives a {@link Throwable}.
     *
     * @param t
     *            {@link Throwable} used to build exception
     */
    public InvalidAlarmJsonException(final Throwable t) {
        super(t);
    }

    /**
     * Constructor that receives a message.
     *
     * @param message
     *            used to build exception
     */
    public InvalidAlarmJsonException(final String message) {
        super(message);
    }
}
