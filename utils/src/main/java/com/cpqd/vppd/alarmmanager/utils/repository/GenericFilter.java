package com.cpqd.vppd.alarmmanager.utils.repository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A generic class filter used by the {@link GenericRepository}.
 *
 * @author Luciano Molinari
 */
public class GenericFilter {

    /**
     * Holds the parameters names and values of the filter.
     */
    private Map<String, Object> paramsNamesAndValues = new LinkedHashMap<>();

    /**
     * The name of the field used to order the query.
     */
    private String orderField;

    /**
     * Constructs a {@link GenericFilter} initializing the {@link #paramsNamesAndValues}
     * with {@code paramName} and {@code paramValue}.
     *
     * @param paramName  The name of the parameter
     * @param paramValue The value of the parameter
     */
    public GenericFilter(final String paramName, final Object paramValue) {
        this.addParam(paramName, paramValue);
    }

    /**
     * Default constructor.
     */
    public GenericFilter() {
    }

    /**
     * Adds a parameter.
     *
     * @param name  The name of the parameter
     * @param value The value of the parameter
     * @return The {@link GenericFilter} itself
     */
    public GenericFilter addParam(final String name, final Object value) {
        this.paramsNamesAndValues.put(name, value);
        return this;
    }

    /**
     * Sets the {@link #orderField}.
     *
     * @param orderField The order field
     * @return The {@link GenericFilter} itself
     */
    public GenericFilter orderField(final String orderField) {
        this.orderField = orderField;
        return this;
    }

    /**
     * @return a {@link Set} with all the parameters names
     */
    public Set<String> getParamsNames() {
        return this.paramsNamesAndValues.keySet();
    }

    /**
     * @param paramName Used to find the parameter value
     * @return The value of the given parameter
     */
    public Object getParamValue(final String paramName) {
        return this.paramsNamesAndValues.get(paramName);
    }

    /**
     * @return the orderField
     */
    public String getOrderField() {
        return this.orderField;
    }

    @Override
    public String toString() {
        return "GenericFilter [paramsNamesAndValues=" + this.paramsNamesAndValues + ", orderField=" + this.orderField
                + "]";
    }
}
