package com.cpqd.vppd.alarmmanager.core.repository;

import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by fabio on 26/12/14.
 */
public class CurrentAlarmsQueryParameters {
    private final List<AlarmSeverity> severities;
    private final Date from;
    private final Date to;
    private final String text;
    private final ObjectId lastId;
    private final Long maxResults;

    public CurrentAlarmsQueryParameters(List<String> severities,
                                        Long from,
                                        Long to,
                                        String text,
                                        String lastId,
                                        Long maxResults) {
        // convert list of severities
        if (severities != null && !severities.isEmpty()) {
            this.severities = Lists.transform(severities, new Function<String, AlarmSeverity>() {
                @Override
                public AlarmSeverity apply(String severityAsString) {
                    return AlarmSeverity.valueOf(severityAsString);
                }
            });
        } else {
            this.severities = null;
        }

        // convert Date parameters
        this.from = from != null ? new DateTime(from, DateTimeZone.UTC).toDate() : null;
        this.to = to != null ? new DateTime(to, DateTimeZone.UTC).toDate() : null;

        // convert the lastId parameter
        this.lastId = lastId != null ? new ObjectId(lastId) : null;

        this.text = text;
        this.maxResults = maxResults;
    }

    public CurrentAlarmsQueryParameters(AlarmSeverity severity,
                                        Date to) {
        this.severities = Arrays.asList(severity);
        this.from = null;
        this.to = to;
        this.text = null;
        this.lastId = null;
        this.maxResults = null;
    }

    public List<AlarmSeverity> getSeverities() {
        return severities;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public String getText() {
        return text;
    }

    public ObjectId getLastId() {
        return lastId;
    }

    public Long getMaxResults() {
        return maxResults;
    }
}
