package com.cpqd.vppd.alarmmanager.alarmquery;

import com.cpqd.vppd.alarmmanager.core.converter.AlarmJsonConverter;
import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmJsonException;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * REST endpoints for alarm queries.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/current")
public class AlarmQueryEndpoint {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmQueryEndpoint.class);

    @Inject
    AlarmQueryHandler alarmQueryHandler;

    @GET
    public Response getCurrentAlarms() throws InvalidAlarmJsonException {
        LOGGER.debug("[] +getCurrentAlarms");

        List<Alarm> alarms = alarmQueryHandler.getCurrentAlarms();

        LOGGER.debug("[] -getCurrentAlarms");

        return Response.ok().entity(alarms).build();
    }
}
