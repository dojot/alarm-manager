package com.cpqd.vppd.alarmmanager.query;

import com.cpqd.vppd.alarmmanager.core.converter.AlarmJsonConverter;
import com.cpqd.vppd.alarmmanager.core.exception.InvalidAlarmJsonException;
import com.cpqd.vppd.alarmmanager.core.model.Alarm;
import com.cpqd.vppd.alarmmanager.core.model.Namespace;
import com.cpqd.vppd.alarmmanager.core.model.AlarmQueryType;
import com.cpqd.vppd.alarmmanager.core.model.AlarmSortOrder;
import com.cpqd.vppd.alarmmanager.core.repository.AlarmQueryFilters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
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
    @Path("namespaces")
    public Response getNamespaces() throws InvalidAlarmJsonException {
        LOGGER.debug("[] +getNamespaces");

        List<Namespace> namespaces = alarmQueryHandler.getNamespaces();
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("namespaces", namespaces);

        LOGGER.debug("[] -getNamespaces");

        return Response.ok().entity(alarmJsonConverter.toJson(responseBody)).build();
    }

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

        AlarmQueryFilters filters = new AlarmQueryFilters(AlarmQueryType.Current,
                namespace, severitiesAsString, from, to, text, lastId, maxResults, null, null);

        Map<String, Object> responseBody = alarmQueryHandler.getCurrentAlarmsByFilterWithMetadata(filters);

        LOGGER.debug("[] -getCurrentAlarms");

        return Response.ok().entity(alarmJsonConverter.toJson(responseBody)).build();
    }

    @GET
    @Path("current/all")
    public Response getAllCurrentAlarms(@QueryParam("severity") final List<String> severitiesAsString,
                                        @QueryParam("from") final Long from,
                                        @QueryParam("to") final Long to,
                                        @QueryParam("text") final String text,
                                        @QueryParam("lastId") final String lastId,
                                        @QueryParam("maxResults") final Long maxResults)
            throws InvalidAlarmJsonException {
        LOGGER.debug("[] +getAllCurrentAlarms");

        AlarmQueryFilters filters = new AlarmQueryFilters(AlarmQueryType.Current,
                "all", severitiesAsString, from, to, text, lastId, maxResults, null, null);

        Map<String, Object> responseBody = alarmQueryHandler.getCurrentAlarmsByFilterWithMetadata(filters);

        LOGGER.debug("[] -getAllCurrentAlarms");

        return Response.ok().entity(alarmJsonConverter.toJson(responseBody)).build();
    }

    @GET
    @Path("history")
    public Response getAlarmsHistory(@QueryParam("severity") final List<String> severitiesAsString,
                                     @QueryParam("from") final Long from,
                                     @QueryParam("to") final Long to,
                                     @QueryParam("text") final String text,
                                     @QueryParam("lastId") final String lastId,
                                     @QueryParam("maxResults") final Long maxResults,
                                     @QueryParam("orderBy") final String orderBy,
                                     @QueryParam("sortOrder") final AlarmSortOrder sortOrder) throws InvalidAlarmJsonException {
        return this.getAlarmsHistory(null, severitiesAsString, from, to, text, lastId, maxResults, orderBy, sortOrder);
    }

    @GET
    @Path("history/{namespace}")
    public Response getAlarmsHistory(@PathParam("namespace") String namespace,
                                     @QueryParam("severity") final List<String> severitiesAsString,
                                     @QueryParam("from") final Long from,
                                     @QueryParam("to") final Long to,
                                     @QueryParam("text") final String text,
                                     @QueryParam("lastId") final String lastId,
                                     @QueryParam("maxResults") final Long maxResults,
                                     @QueryParam("orderBy") final String orderBy,
                                     @QueryParam("sortOrder") final AlarmSortOrder sortOrder) throws InvalidAlarmJsonException {
        LOGGER.debug("[] +getAlarmsHistory");

        AlarmQueryFilters filters = new AlarmQueryFilters(AlarmQueryType.History,
                namespace, severitiesAsString, from, to, text, lastId, maxResults, orderBy, sortOrder);

        List<Alarm> alarms = alarmQueryHandler.getAlarmsByFilters(filters);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("alarms", alarms);

        LOGGER.debug("[] -getAlarmsHistory");

        return Response.ok().entity(alarmJsonConverter.toJson(responseBody)).build();
    }

    @GET
    @Path("history/all")
    public Response getAllAlarmsHistory(@QueryParam("severity") final List<String> severitiesAsString,
                                        @QueryParam("from") final Long from,
                                        @QueryParam("to") final Long to,
                                        @QueryParam("text") final String text,
                                        @QueryParam("lastId") final String lastId,
                                        @QueryParam("maxResults") final Long maxResults,
                                        @QueryParam("orderBy") final String orderBy,
                                        @QueryParam("sortOrder") final AlarmSortOrder sortOrder)
            throws InvalidAlarmJsonException {
        LOGGER.debug("[] +getAllAlarmsHistory");

        AlarmQueryFilters filters = new AlarmQueryFilters(AlarmQueryType.History,
                "all", severitiesAsString, from, to, text, lastId, maxResults, orderBy, sortOrder);

        List<Alarm> alarms = alarmQueryHandler.getAlarmsByFilters(filters);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("alarms", alarms);

        LOGGER.debug("[] -getAllAlarmsHistory");

        return Response.ok().entity(alarmJsonConverter.toJson(responseBody)).build();
    }
}
