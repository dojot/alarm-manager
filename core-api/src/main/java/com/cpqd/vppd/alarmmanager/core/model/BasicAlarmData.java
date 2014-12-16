package com.cpqd.vppd.alarmmanager.core.model;

import com.cpqd.vppd.alarmmanager.utils.repository.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Class that defines a basic set of common data shared between all the concrete classes
 * that represent alarm-related entities, such as {@link AlarmEvent} and {@link Alarm}.
 */
@MappedSuperclass
public abstract class BasicAlarmData {// extends BaseEntity<BasicAlarmData> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Type(type = "objectid")
    @JsonIgnore
    private String id;

    @NotNull
    private String domain;

    private String description;

    @NotNull
    private AlarmSeverity severity;

    @NotNull
    @NotEmpty
    @ElementCollection
    private Set<DomainSpecificField> primarySubject;

    @ElementCollection
    private Set<DomainSpecificField> additionalData;

    public BasicAlarmData() {

    }

    public BasicAlarmData(BasicAlarmData other) {
        this.domain = other.domain;
        this.description = other.description;
        this.severity = other.severity;
        this.primarySubject = ImmutableSet.copyOf(other.primarySubject);
        this.additionalData = ImmutableSet.copyOf(other.additionalData);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Set<DomainSpecificField> getPrimarySubject() {
        return primarySubject;
    }

    public void setPrimarySubject(Set<DomainSpecificField> primarySubject) {
        this.primarySubject = primarySubject;
    }

    public Set<DomainSpecificField> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Set<DomainSpecificField> additionalData) {
        this.additionalData = additionalData;
    }
}
