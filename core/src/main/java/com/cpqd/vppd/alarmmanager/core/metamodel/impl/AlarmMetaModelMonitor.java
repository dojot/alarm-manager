package com.cpqd.vppd.alarmmanager.core.metamodel.impl;

import com.cpqd.vppd.alarmmanager.core.converter.AlarmMetaModelXmlConverter;
import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmMetaModelXmlException;
import com.cpqd.vppd.alarmmanager.core.exception.UnknownAlarmMetaModelException;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModel;
import com.cpqd.vppd.alarmmanager.core.metamodel.AlarmMetaModelManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.*;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Class which defines a method that runs in a separate thread watching then file system for metamodel changes.
 */
@Singleton
@Startup
@Lock(LockType.READ)
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

    @Resource
    private ManagedThreadFactory threadFactory;

    private boolean keepRunning = true;

    @PostConstruct
    public void start() {
        Collection<File> xmlFiles = FileUtils.listFiles(FileUtils.getFile(metaModelManager.getBaseMetaModelsPath()),
                FileFilterUtils.suffixFileFilter(".xml"), FalseFileFilter.INSTANCE);

        for (File file : xmlFiles) {
            try {
                String xml = FileUtils.readFileToString(file);
                AlarmMetaModel metaModel = metaModelXmlConverter.fromXml(xml);

                // register metamodel
                String baseFileName = FilenameUtils.getBaseName(file.getName());
                String domain = StringUtils.substringAfterLast(baseFileName, ".");
                String namespace = "";
                if (StringUtils.isBlank(domain)) {
                    // file name does not contain dots, the whole name is the domain
                    domain = baseFileName;
                } else {
                    namespace = StringUtils.substringBeforeLast(baseFileName, ".");
                }

                if (((StringUtils.isBlank(namespace) && StringUtils.isBlank(metaModel.getNamespace())) || namespace.equals(metaModel.getNamespace()))
                        && domain.equals(metaModel.getDomain())) {
                    metaModelManager.addOrUpdateMetaModel(metaModel);
                } else {
                    LOGGER.error("Namespace or domain specified in file contents do not" +
                            " match the ones extracted from the file name. Metamodel ignored");
                }
            } catch (IOException | InvalidAlarmMetaModelXmlException e) {
                LOGGER.error("Error parsing XML file {}: {}", file.getName(), e.getMessage());
            }
        }

        threadFactory.newThread(
            new Runnable() {
                @Override
                public void run() {
                    Boolean valid = Boolean.TRUE;

                    Path monitoredPath = Paths.get(metaModelManager.getBaseMetaModelsPath());

                    try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                        monitoredPath.register(watchService,
                                StandardWatchEventKinds.ENTRY_CREATE,
                                StandardWatchEventKinds.ENTRY_MODIFY,
                                StandardWatchEventKinds.ENTRY_DELETE);

                        LOGGER.debug("Monitoring thread running");

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
                                                metaModelManager.deleteMetaModelForNamespaceAndDomain(namespace, domain);
                                                LOGGER.info("Metamodel for namespace '{}' and domain '{}' removed" +
                                                        " from known models", namespace, domain);
                                            } catch (UnknownAlarmMetaModelException e) {
                                                LOGGER.warn("Metamodel for namespace '{}' and domain '{}' was" +
                                                        " not registered", namespace, domain);
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

                                            // validate if the namespace and domain specified in the file contents
                                            // match the ones extracted from the file name
                                            if (((StringUtils.isBlank(namespace) && StringUtils.isBlank(metaModel.getNamespace())) || namespace.equals(metaModel.getNamespace()))
                                                    && domain.equals(metaModel.getDomain())) {
                                                metaModelManager.addOrUpdateMetaModel(metaModel);
                                            } else {
                                                LOGGER.error("Namespace or domain specified in file contents do not" +
                                                        " match the ones extracted from the file name. Metamodel ignored");
                                            }
                                        }
                                    } else {
                                        LOGGER.debug("Ignoring irrelevant file {}", filePath);
                                    }
                                }

                                valid = watchKey.reset();
                            }
                        } while (valid && keepRunning);

                    } catch (IOException e) {
                        LOGGER.error("Metamodel monitoring service could not be started", e);
                    } catch (InterruptedException e) {
                        LOGGER.info("Metamodel monitoring thread execution aborted");
                    }

                    LOGGER.debug("Monitoring thread shutting down");
                }
            }
        ).start();

        LOGGER.info("Known metamodels are loaded and path is being monitored...");
    }

    @PreDestroy
    public void stop() {
        keepRunning = false;
    }
}
