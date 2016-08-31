package com.cpqd.vppd.alarmmanager.core.model;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Class that represents an namespace.
 */
@Entity
public class Namespace {
    @NotNull
    public String namespace;

    public Namespace() {

    }
}
