package com.cpqd.vppd.alarmmanager.mongoconnector;

import com.mongodb.ServerAddress;

import java.io.IOException;
import java.util.Properties;

/**
 * Holds properties to access MongoDB. For now, it only holds one serverAddress.
 * Future implementations may also hold:
 * <ul>
 * <li>A list of {@link com.mongodb.ServerAddress}
 * <li>Authentication data per DB
 * </ul>
 *
 * @author Luciano Molinari
 */
public final class ConnectionProperties {

    /**
     * {@link com.mongodb.ServerAddress} already parsed from the properties.
     */
    private final ServerAddress serverAddress;

    /**
     * Name of the property that holds mongo host in the format "host:port".
     */
    public static final String PROPERTY_MONGO_HOST = "mongo.host";

    /**
     * Creates a {@link ConnectionProperties} based on the properties given as
     * parameter.
     *
     * @param properties Properties used to build the {@link ConnectionProperties}
     * @return {@link ConnectionProperties}
     * @throws IllegalArgumentException If a property is not given or given in an invalid format
     */
    public static ConnectionProperties load(final Properties properties) {
        String host = properties.getProperty(PROPERTY_MONGO_HOST);
        try {
            return new ConnectionProperties(parserHost(host));
        } catch (Exception e) {
            throw new IllegalArgumentException("The value of the system property " + PROPERTY_MONGO_HOST + " (" + host
                    + ") is invalid..", e);
        }
    }

    /**
     * Constructor that takes a {@link ServerAddress} as parameter.
     *
     * @param serverAddress The serverAddress
     */
    public ConnectionProperties(final ServerAddress serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * @return {@link ServerAddress} configured
     */
    public ServerAddress getServerAddress() {
        return this.serverAddress;
    }

    /**
     * Creates a {@link ServerAddress} based on a String in the format
     * "host:port".
     *
     * @param host Host to be parsed
     * @return {@link ServerAddress}
     * @throws java.io.IOException      If {@link ServerAddress} does not exist
     * @throws IllegalArgumentException If host is null
     */
    private static ServerAddress parserHost(final String host) throws IOException {
        if (host == null) {
            throw new IllegalArgumentException("The system property " + PROPERTY_MONGO_HOST + " must be specified");
        }

        String[] parts = host.split(":");
        String parsedHost = parts[0] != null ? parts[0].trim() : null;
        int parsedPort = parts[1] != null ? Integer.parseInt(parts[1]) : -1;

        return new ServerAddress(parsedHost, parsedPort);
    }

}
