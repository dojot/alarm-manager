package com.cpqd.vppd.alarmmanager.core.exception;

/**
 * Created by fabio on 09/12/14.
 */
public class AlarmNotPresentException extends Exception {
    private static final long serialVersionUID = 4827968601258740242L;

    /**
     * Constructor that receives a {@link Throwable}.
     *
     * @param t
     *            {@link Throwable} used to build exception
     */
    public AlarmNotPresentException(final Throwable t) {
        super(t);
    }

    /**
     * Constructor that receives a message.
     *
     * @param message
     *            used to build exception
     */
    public AlarmNotPresentException(final String message) {
        super(message);
    }
}
