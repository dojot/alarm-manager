package com.cpqd.vppd.alarmmanager.core.model.metadata;

import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.google.common.base.Strings;

/**
 * Created by fabio on 30/12/14.
 */
public class NamespaceAndSeverity {
    private String namespace;
    private AlarmSeverity severity;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = Strings.nullToEmpty(namespace);
    }

    public AlarmSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlarmSeverity severity) {
        this.severity = severity;
    }
}
