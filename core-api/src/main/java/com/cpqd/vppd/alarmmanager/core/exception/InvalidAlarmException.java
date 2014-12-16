package com.cpqd.vppd.alarmmanager.core.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fabio on 16/12/14.
 */
public class InvalidAlarmException extends Exception {

    /**
     * Generated serialVersionUID.
     */
    private static final long serialVersionUID = 1209755244530922395L;

    /**
     * Enum to define the possible reasons why an alarm is not valid.
     */
    public enum Cause {
        /** Used when the domain of the alarm was not informed. */
        NULL_DOMAIN,
        /** Used when the domain of the alarm is not registered with the manager. */
        UNKNOWN_DOMAIN,
        /** Used when the severity of the alarm was not informed. */
        NULL_SEVERITY,
        /** Used when the primary subject of the alarm was not informed. */
        NULL_PRIMARY_SUBJECT,
        /** Used when the primary subject of the alarm does not match the metamodel. */
        MALFORMED_PRIMARY_SUBJECT,
        /** Used when the additional data of the alarm does not match the metamodel. */
        MALFORMED_ADDITIONAL_DATA
    }

    /**
     * Mapping between fields and {@link Cause}.
     */
    private static final Map<String, Cause> NULL_FIELDS_MAPPED_TO_CAUSES;

    /**
     * Error cause of the exception.
     */
    private Cause errorCause;

    /**
     * Initializes the map containing the relation between fields that may be null and the related {@link Cause}.
     */
    static {
        NULL_FIELDS_MAPPED_TO_CAUSES = new HashMap<>();
        NULL_FIELDS_MAPPED_TO_CAUSES.put("domain", Cause.NULL_DOMAIN);
        NULL_FIELDS_MAPPED_TO_CAUSES.put("severity", Cause.NULL_SEVERITY);
        NULL_FIELDS_MAPPED_TO_CAUSES.put("primarySubject", Cause.NULL_PRIMARY_SUBJECT);
    }

    /**
     * Creates an {@link InvalidAlarmException} based on an errorCause.
     *
     * @param errorCause The error cause
     */
    public InvalidAlarmException(final Cause errorCause) {
        this.errorCause = errorCause;
    }

    /**
     * @return the errorCause
     */
    public Cause getErrorCause() {
        return this.errorCause;
    }

    /**
     * Builds an {@link InvalidAlarmException} based on a null field parameter.
     *
     * @param field used to build the exception.
     * @return The {@link InvalidAlarmException} with the proper errorCause
     * @throws IllegalArgumentException If there is no exception mapped for the given field
     */
    public static InvalidAlarmException buildFromNullField(final String field) {
        Cause errorCause = NULL_FIELDS_MAPPED_TO_CAUSES.get(field);
        if (errorCause == null) {
            throw new IllegalArgumentException("The field " + field + " was not filled properly.");
        }
        return new InvalidAlarmException(errorCause);
    }

    @Override
    public String toString() {
        return "InvalidAlarmException{" +
                "errorCause=" + errorCause +
                '}';
    }
}
