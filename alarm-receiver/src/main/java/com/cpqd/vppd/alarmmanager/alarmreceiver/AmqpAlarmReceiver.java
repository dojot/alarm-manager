package com.cpqd.vppd.alarmmanager.alarmreceiver;

import com.rabbitmq.client.*;
import net.jodah.lyra.ConnectionOptions;
import net.jodah.lyra.Connections;
import net.jodah.lyra.config.Config;
import net.jodah.lyra.config.RecoveryPolicy;
import net.jodah.lyra.config.RetryPolicy;
import net.jodah.lyra.util.Duration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.io.IOException;

/**
 * This class is responsible for receiving alarm notifications through RabbitMQ/AMQP.
 * <p/>
 * This class is defined as a singleton and thus, there is only one consumer instance. However, as soon as the
 * message is received, it is dispatched to {@link com.cpqd.vppd.alarmmanager.alarmreceiver.AlarmEventProcessor#onAlarmReceived(String)} EJB, that handles
 * events asynchronously, releasing the receiver.
 *
 * @author Fabio Margarido
 */
@Startup
@Singleton
public class AmqpAlarmReceiver {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AmqpAlarmReceiver.class);

    /**
     * Name of the exchange to connect.
     */
    private static final String EXCHANGE_NAME = "alarms.exchange";

    /**
     * Name of the queue to connect.
     */
    private static final String QUEUE_NAME = "alarms.queue";

    /**
     * Routing key used to receive messages.
     */
    private static final String ROUTING_KEY_NAME = "alarms";

    /**
     * AMQP connection. It is held as an instance variable so it can be closed in the @PreDestroy method.
     */

    private Connection connection;
    /**
     * Channel AMQP connection. It is held as an instance variable so it can be closed in the @PreDestroy method.
     */
    private Channel channel;

    /**
     * Instance of {@link com.cpqd.vppd.alarmmanager.alarmreceiver.AlarmEventProcessor}.
     */
    @Inject
    private AlarmEventProcessor processor;

    /**
     * Initializer method. Responsible for starting AMQP receiver.
     */
    @PostConstruct
    public void init() {
        LOGGER.debug("Initializing AMQP receiver");
        try {
            this.startReceiver();
            LOGGER.debug("AMQP receiver initialized");
        } catch (IOException e) {
            LOGGER.error("Error while starting AMQP client", e);
        }
    }

    /**
     * Responsible for releasing AMQP channel and connection.
     */
    @PreDestroy
    private void destroyReceiver() {
        LOGGER.info("Destroying resources");
        try {
            this.channel.close();
            this.connection.close();
        } catch (IOException e) {
            LOGGER.error("Error while closing AMQP channel and connection", e);
        }
    }

    /**
     * Responsible for starting AMQP receiver.
     *
     * @throws IOException If some error occurs while establishing connection.
     */
    private void startReceiver() throws IOException {
        Config recoveryParameters = new Config().withRecoveryPolicy(new RecoveryPolicy().withMaxAttempts(-1)
                .withBackoff(Duration.seconds(1), Duration.seconds(2)))
                .withRetryPolicy(new RetryPolicy().withBackoff(Duration.seconds(1), Duration.seconds(4))
                        .withMaxAttempts(-1));

        ConnectionOptions options = new ConnectionOptions().
                withHost(this.getHostAMQPServer()).withPort(this.getPortAMQPServer());

        this.connection = Connections.create(options, recoveryParameters);
        this.channel = this.connection.createChannel();

        this.channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
        this.channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        this.channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY_NAME);

        this.channel.basicConsume(QUEUE_NAME, true, new DefaultConsumer(this.channel) {
            /*
             * (non-Javadoc)
             *
             * @see com.rabbitmq.client.DefaultConsumer#handleDelivery(java.lang.String, com.rabbitmq.client.Envelope,
             * com.rabbitmq.client.AMQP.BasicProperties, byte[])
             */
            @Override
            public void handleDelivery(final String consumerTag, final Envelope envelope,
                                       final AMQP.BasicProperties properties, final byte[] body)
                    throws IOException {
                try {
                    AmqpAlarmReceiver.this.processor.onAlarmReceived(new String(body));
                } catch (Exception e) {
                    LOGGER.error("Unexpected error while handling event", e);
                }
            }
        });
    }

    /**
     * Discovers the host of the AMQP Server to which the application must connect.
     * It looks for the system property "rabbit.host" and, if this property is not set,
     * uses the default value "localhost".
     *
     * @return The host to connect
     */
    private String getHostAMQPServer() {
        String host = System.getProperty("rabbit.host");
        if (StringUtils.isBlank(host)) {
            LOGGER.warn("No value found for property rabbit.host.");
            host = "localhost";
        }
        LOGGER.debug("Connection to AMQP Server at host " + host);
        return host;
    }

    /**
     * Discovers the host of the AMQP Server to which the application must connect.
     * It looks for the system property "rabbit.host" and, if this property is not set,
     * uses the default value "localhost".
     *
     * @return The host to connect
     */
    private int getPortAMQPServer() {
        String port = System.getProperty("rabbit.port");
        if (StringUtils.isBlank(port)) {
            LOGGER.warn("No value found for property rabbit.port.");
            port = "5672";
        }
        LOGGER.debug("Connection to AMQP Server at port " + port);
        return Integer.parseInt(port);
    }
}
