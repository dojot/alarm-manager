package com.cpqd.vppd.alarmmanager.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableMap;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;
import org.jongo.marshall.jackson.oid.Id;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class that defines a basic set of common data shared between all the concrete classes
 * that represent alarm-related entities, such as {@link AlarmEvent} and {@link Alarm}.
 */
public abstract class BasicAlarmData {
    @Id
    protected ObjectId id;

    private String namespace;

    @NotNull
    protected String domain;

    protected String description;

    @NotNull
    protected AlarmSeverity severity;

    @NotNull
    @NotEmpty
    @JsonDeserialize(as = TreeMap.class)
    protected Map<String, Object> primarySubject;

    @JsonDeserialize(as = TreeMap.class)
    protected Map<String, Object> additionalData;

    public BasicAlarmData() {

    }

    public BasicAlarmData(BasicAlarmData other) {
        this.namespace = other.namespace;
        this.domain = other.domain;
        this.description = other.description;
        this.severity = other.severity;
        this.primarySubject = ImmutableMap.copyOf(other.primarySubject);
        this.additionalData = ImmutableMap.copyOf(other.additionalData);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AlarmSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlarmSeverity severity) {
        this.severity = severity;
    }

    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Map<String, Object> additionalData) {
        this.additionalData = additionalData;
    }

    public Map<String, Object> getPrimarySubject() {
        return primarySubject;
    }

    public void setPrimarySubject(Map<String, Object> primarySubject) {
        this.primarySubject = primarySubject;
    }
}
