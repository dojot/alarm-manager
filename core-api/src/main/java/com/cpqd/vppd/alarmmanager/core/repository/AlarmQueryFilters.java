package com.cpqd.vppd.alarmmanager.core.repository;

import com.cpqd.vppd.alarmmanager.core.model.AlarmQueryType;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSortOrder;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.Date;
import java.util.List;

/**
 * Class which holds parameter values to be used to filter current alarm queries.
 */
public class AlarmQueryFilters {
    private final AlarmQueryType type;
    private final String namespace;
    private final List<AlarmSeverity> severities;
    private final Date from;
    private final Date to;
    private final String text;
    private final ObjectId lastId;
    private final Long maxResults;
    private final String orderBy;
    private final AlarmSortOrder sortOrder;

    public AlarmQueryFilters(AlarmQueryType type,
                             String namespace,
                             List<String> severities,
                             Long from,
                             Long to,
                             String text,
                             String lastId,
                             Long maxResults,
                             String orderBy,
                             AlarmSortOrder sortOrder) {
        this.type = type;
        this.namespace = namespace;

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
        this.orderBy = orderBy;
        this.sortOrder = sortOrder;
    }

    public AlarmQueryType getType() {
        return type;
    }

    public String getNamespace() {
        return namespace;
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

    public String getOrderBy() {
        return orderBy;
    }

    public AlarmSortOrder getSortOrder() {
        return this.sortOrder;
    }
}
