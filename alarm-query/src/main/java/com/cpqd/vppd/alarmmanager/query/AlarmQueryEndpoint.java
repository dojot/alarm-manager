package com.cpqd.vppd.alarmmanager.query;

import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmJsonException;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSeverity;
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
    private AlarmQueryHandler alarmQueryHandler;

    @GET
    public Response getCurrentAlarms(@QueryParam("severity") final List<String> severitiesAsString,
                                     @QueryParam("from") final Long from,
                                     @QueryParam("to") final Long to) throws InvalidAlarmJsonException {
        LOGGER.debug("[] +getCurrentAlarms");

        // convert filters into expected data types
        List<AlarmSeverity> severities = null;
        if (severitiesAsString != null && !severitiesAsString.isEmpty()) {
            severities = Lists.transform(severitiesAsString, new Function<String, AlarmSeverity>() {
                @Override
                public AlarmSeverity apply(String severityAsString) {
                    return AlarmSeverity.valueOf(severityAsString);
                }
            });
        }

        Date fromDate = from != null ? new DateTime(from, DateTimeZone.UTC).toDate() : null;
        Date toDate = to != null ? new DateTime(to, DateTimeZone.UTC).toDate() : null;

        List<Alarm> alarms = alarmQueryHandler.getCurrentAlarms(severities, fromDate, toDate);

        LOGGER.debug("[] -getCurrentAlarms");

        return Response.ok().entity(alarms).build();
    }
}
