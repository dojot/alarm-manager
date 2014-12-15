package com.cpqd.vppd.alarmmanager.core.metamodel.impl;

import com.cpqd.vppd.alarmmanager.core.exception.UnknownAlarmMetaModelException;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModel;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModelManager;
import com.cpqd.vppd.alarmmanager.utils.AlarmMetaModelXmlConverter;
import com.cpqd.vppd.alarmmanager.utils.exception.InvalidAlarmMetaModelXmlException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Class which defines a method that runs in a separate thread watching then file system for metamodel changes.
 */
@Stateless
public class AlarmMetaModelMonitor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmMetaModelMonitor.class);

    /**
     * The {@link AlarmMetaModelManagerImpl} instance which keeps the
     * list of registered and valid metamodels.
     */
    @Inject
    private AlarmMetaModelManager metaModelManager;


    /**
     * Converter to generate metamodel instances from the XML files.
     */
    @Inject
    private AlarmMetaModelXmlConverter metaModelXmlConverter;

    private WatchService watchService;

    private Path monitoredPath;

    /**
     * The session context is used to verify if the monitoring thread's execution has been cancelled.
     */
    @Resource
    SessionContext sessionContext;

    @Asynchronous
    public Future<Boolean> startMetaModelMonitoring() {
        Boolean valid = Boolean.TRUE;

        Path monitoredPath = Paths.get(metaModelManager.getBaseMetaModelsPath());

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            monitoredPath.register(watchService,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                    StandardWatchEventKinds.ENTRY_DELETE);

            do {
                WatchKey watchKey = watchService.poll(10, TimeUnit.SECONDS);
                if (watchKey != null) {
                    for (WatchEvent<?> event : watchKey.pollEvents()) {
                        final WatchEvent.Kind<?> eventKind = event.kind();
                        final Path filePath = (Path) event.context();

                        if (filePath.toString().endsWith(".xml")) {
                            AlarmMetaModel metaModel;

                            String baseFileName = FilenameUtils.getBaseName(filePath.toString());
                            String domain = StringUtils.substringAfterLast(baseFileName, ".");
                            String namespace = "";

                            if (StringUtils.isBlank(domain)) {
                                // file name does not contain dots, the whole name is the domain
                                domain = baseFileName;
                            } else {
                                namespace = StringUtils.substringBeforeLast(baseFileName, ".");
                            }

                            if (StandardWatchEventKinds.ENTRY_DELETE.name().equals(eventKind.name())) {
                                // deregister metamodel extracted from the deleted file name
                                try {
                                    metaModelManager.deleteMetaModelForDomain(domain);
                                    LOGGER.info("Metamodel for domain {} removed from known models", domain);
                                } catch (UnknownAlarmMetaModelException e) {
                                    LOGGER.warn("Metamodel for domain {} was not registered", domain);
                                }

                            } else if (StandardWatchEventKinds.ENTRY_CREATE.name().equals(eventKind.name())
                                    || StandardWatchEventKinds.ENTRY_MODIFY.name().equals(eventKind.name())) {
                                try {
                                    String xml = new String(Files.readAllBytes(
                                            Paths.get(monitoredPath.toString(), filePath.toString())));
                                    metaModel = metaModelXmlConverter.fromXml(xml);

                                } catch (InvalidAlarmMetaModelXmlException e) {
                                    LOGGER.error("Error parsing metamodel XML. Ignoring...", e);
                                    continue;
                                } catch (Exception e) {
                                    LOGGER.error("Unhandled error in metamodel monitoring thread", e);
                                    continue;
                                }

                                // validate if the domain specified in the file contents matches the domain extracted
                                // from the file name
                                if (domain.equals(metaModel.getDomain())) {
                                    metaModelManager.addOrUpdateMetaModel(metaModel);
                                } else {
                                    LOGGER.error("Domain specified in file contents does not match domain extracted" +
                                            " from the file name. Metamodel ignored");
                                }
                            }
                        } else {
                            LOGGER.debug("Ignoring irrelevant file {}", filePath);
                        }
                    }

                    valid = watchKey.reset();
                }
            } while (valid && !sessionContext.wasCancelCalled());

        } catch (IOException e) {
            LOGGER.error("Metamodel monitoring service could not be started", e);
        } catch (InterruptedException e) {
            LOGGER.error("Metamodel monitoring thread execution aborted", e);
        }

        LOGGER.debug("Monitoring thread shutting down");

        return new AsyncResult<>(valid);
    }

}
