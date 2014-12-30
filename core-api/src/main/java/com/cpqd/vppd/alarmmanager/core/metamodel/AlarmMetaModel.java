package com.cpqd.vppd.alarmmanager.core.metamodel;

import com.cpqd.vppd.alarmmanager.core.model.DomainSpecificField;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.google.common.base.Strings;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents the metamodel of an alarm domain.
 */
@JacksonXmlRootElement(localName = "alarm-metamodel")
public class AlarmMetaModel {
    private String domain;
    private String namespace;
    @JacksonXmlElementWrapper(localName = "primary-subject")
    @JacksonXmlProperty(localName = "primary-field")
    private Set<DomainSpecificField> primarySubject = new HashSet<>();
    @JacksonXmlElementWrapper(localName = "additional-data")
    @JacksonXmlProperty(localName = "additional-field")
    private Set<DomainSpecificField> additionalData = new HashSet<>();

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = Strings.nullToEmpty(namespace);
    }

    public Set<DomainSpecificField> getPrimarySubject() {
        return primarySubject;
    }

    public void setPrimarySubject(Set<DomainSpecificField> primarySubject) {
        this.primarySubject = primarySubject;
    }

    public void addPrimarySubjectField(DomainSpecificField field) {
        this.primarySubject.add(field);
    }

    public Set<DomainSpecificField> getAdditionalData() {
        return additionalData;
    }

    public void setAdditionalData(Set<DomainSpecificField> additionalData) {
        this.additionalData = additionalData;
    }

    public void addAdditionalDataField(DomainSpecificField field) {
        this.additionalData.add(field);
    }
}
