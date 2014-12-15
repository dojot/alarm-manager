package com.cpqd.vppd.alarmmanager.core.metamodel.impl;

import com.cpqd.vppd.alarmmanager.core.exception.UnknownAlarmMetaModelException;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModel;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * and monitoring it for changes.
 */
@Singleton
public class AlarmMetaModelManagerImpl implements AlarmMetaModelManager {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmMetaModelManagerImpl.class);

    private Map<String, AlarmMetaModel> knownMetaModels = new HashMap<>();

    public void addOrUpdateMetaModel(AlarmMetaModel metaModel) {
        knownMetaModels.put(metaModel.getDomain(), metaModel);
        LOGGER.debug("Successfully registered metamodel for domain {}", metaModel.getDomain());
    }

    public void deleteMetaModelForDomain(String metaModelDomain) throws UnknownAlarmMetaModelException {
        if (knownMetaModels.remove(metaModelDomain) != null) {
            LOGGER.debug("Successfully deleted metamodel for domain {}", metaModelDomain);
        } else {
            throw new UnknownAlarmMetaModelException(String.format("Domain %s was not registered", metaModelDomain));
        }
    }

    public AlarmMetaModel getMetaModelForDomain(String domain) throws UnknownAlarmMetaModelException {
        AlarmMetaModel metaModel = knownMetaModels.get(domain);
        if (metaModel == null) {
            throw new UnknownAlarmMetaModelException(String.format("Domain %s is not registered", domain));
        }

        return metaModel;
    }

    /**
     * @return Base path of the files.
     */
    public String getBaseMetaModelsPath() {
        return System.getProperty("base.alarm.path") + "/metamodel/";
    }
}
