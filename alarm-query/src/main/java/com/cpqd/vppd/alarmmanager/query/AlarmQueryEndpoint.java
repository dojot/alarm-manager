package com.cpqd.vppd.alarmmanager.query;

import com.cpqd.vppd.alarmmanager.core.converter.AlarmJsonConverter;
import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmJsonException;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
import com.cpqd.vppd.alarmmanager.core.repository.CurrentAlarmsQueryParameters;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private AlarmQueryHandler alarmQueryHandler;

    @Inject
    private AlarmJsonConverter alarmJsonConverter;

    @GET
    public Response getCurrentAlarms(@QueryParam("severity") final List<String> severitiesAsString,
                                     @QueryParam("from") final Long from,
                                     @QueryParam("to") final Long to,
                                     @QueryParam("text") final String text,
                                     @QueryParam("lastId") final String lastId,
                                     @QueryParam("maxResults") final Long maxResults) throws InvalidAlarmJsonException {
        LOGGER.debug("[] +getCurrentAlarms");

        CurrentAlarmsQueryParameters parameters = new CurrentAlarmsQueryParameters(severitiesAsString,
                from, to, text, lastId, maxResults);

        Map<String, Object> responseBody = alarmQueryHandler.getCurrentAlarmsAndMetadata(parameters);

        LOGGER.debug("[] -getCurrentAlarms");

        return Response.ok().entity(alarmJsonConverter.toJson(responseBody)).build();
    }
}
