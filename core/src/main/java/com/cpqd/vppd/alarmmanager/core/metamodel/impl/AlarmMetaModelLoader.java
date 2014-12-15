package com.cpqd.vppd.alarmmanager.core.metamodel.impl;

import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModel;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModelManager;
import com.cpqd.vppd.alarmmanager.utils.AlarmMetaModelXmlConverter;
import com.cpqd.vppd.alarmmanager.utils.exception.InvalidAlarmMetaModelXmlException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Future;

/**
 * Class responsible for loading alarm metamodels from the file system.
 */
@Startup
@Singleton
public class AlarmMetaModelLoader {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmMetaModelLoader.class);

    @Inject
    private AlarmMetaModelManager metaModelManager;

    @Inject
    private AlarmMetaModelMonitor metaModelMonitor;

    @Inject
    private AlarmMetaModelXmlConverter metaModelXmlConverter;

    private Future<Boolean> future;

    @PostConstruct
    public void loadMetaModels() {
        Collection<File> xmlFiles = FileUtils.listFiles(FileUtils.getFile(metaModelManager.getBaseMetaModelsPath()),
                FileFilterUtils.suffixFileFilter(".xml"), FalseFileFilter.INSTANCE);

        for (File file : xmlFiles) {
            try {
                String xml = FileUtils.readFileToString(file);
                AlarmMetaModel metaModel = metaModelXmlConverter.fromXml(xml);

                // register metamodel
                metaModelManager.addOrUpdateMetaModel(metaModel);
            } catch (IOException | InvalidAlarmMetaModelXmlException e) {
                LOGGER.error("Error parsing XML file {}: {}", file.getName(), e.getMessage());
            }
        }

        future = metaModelMonitor.startMetaModelMonitoring();

        LOGGER.info("Known metamodels are loaded and path is being monitored...");
    }

    @PreDestroy
    public void tearDown() {
        if (!future.isDone()) {
            LOGGER.debug("Cancelling metamodel monitoring thread execution");
            future.cancel(true);
        }
    }
}
