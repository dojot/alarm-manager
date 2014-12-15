package com.cpqd.vppd.alarmmanager.core.metamodel;

import com.cpqd.vppd.alarmmanager.core.exception.UnknownAlarmMetaModelException;

/**
 * Created by fabio on 15/12/14.
 */
public interface AlarmMetaModelManager {
    public void addOrUpdateMetaModel(AlarmMetaModel metaModel);

    public void deleteMetaModelForDomain(String metaModelDomain) throws UnknownAlarmMetaModelException;

    public AlarmMetaModel getMetaModelForDomain(String domain) throws UnknownAlarmMetaModelException;

    /**
     * @return Base path of the files.
     */
    public String getBaseMetaModelsPath();
}
