package com.cpqd.vppd.alarmmanager.query;

import com.cpqd.vppd.alarmmanager.core.converter.AlarmJsonConverter;
import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmJsonException;
import com.cpqd.vppd.alarmmanager.core.repository.CurrentAlarmsQueryFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * REST endpoints for alarm queries.
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/alarms")
public class AlarmQueryEndpoint {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmQueryEndpoint.class);

    @Inject
    private AlarmQueryHandler alarmQueryHandler;

    @Inject
    private AlarmJsonConverter alarmJsonConverter;

    @GET
    @Path("current")
    public Response getCurrentAlarms(@QueryParam("severity") final List<String> severitiesAsString,
                                     @QueryParam("from") final Long from,
                                     @QueryParam("to") final Long to,
                                     @QueryParam("text") final String text,
                                     @QueryParam("lastId") final String lastId,
                                     @QueryParam("maxResults") final Long maxResults) throws InvalidAlarmJsonException {
        return this.getCurrentAlarms(null, severitiesAsString, from, to, text, lastId, maxResults);
    }

    @GET
    @Path("current/{namespace}")
    public Response getCurrentAlarms(@PathParam("namespace") String namespace,
                                     @QueryParam("severity") final List<String> severitiesAsString,
                                     @QueryParam("from") final Long from,
                                     @QueryParam("to") final Long to,
                                     @QueryParam("text") final String text,
                                     @QueryParam("lastId") final String lastId,
                                     @QueryParam("maxResults") final Long maxResults) throws InvalidAlarmJsonException {
        LOGGER.debug("[] +getCurrentAlarms");

        CurrentAlarmsQueryFilters parameters = new CurrentAlarmsQueryFilters(namespace, severitiesAsString,
                from, to, text, lastId, maxResults);

        Map<String, Object> responseBody = alarmQueryHandler.getCurrentAlarmsAndMetadata(parameters);

        LOGGER.debug("[] -getCurrentAlarms");

        return Response.ok().entity(alarmJsonConverter.toJson(responseBody)).build();
    }
}
