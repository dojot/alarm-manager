package com.cpqd.vppd.alarmmanager.core.metamodel;

import com.cpqd.vppd.alarmmanager.core.exception.UnknownAlarmMetaModelException;

/**
 * Interface that must be implemented by the class responsible for managing meta models.
 */
public interface AlarmMetaModelManager {
    public void addOrUpdateMetaModel(AlarmMetaModel metaModel);

    public void deleteMetaModelForNamespaceAndDomain(String namespace, String domain) throws UnknownAlarmMetaModelException;

    public AlarmMetaModel getMetaModelForNamespaceAndDomain(String namespace, String domain) throws UnknownAlarmMetaModelException;

    /**
     * @return Base path of the files.
     */
    public String getBaseMetaModelsPath();
}
