package com.cpqd.vppd.alarmmanager.utils.repository;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * Generic DAO with methods for most of the persistence operations.
 *
 * @param <T>  Type of the entity to be used id the db operations
 * @param <ID> ID type of the entity
 * @author Luciano Molinari
 */
public abstract class GenericRepository<T, ID extends Serializable> {
    /**
     * {@link javax.persistence.PersistenceContext} used to access DB.
     */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * @return the {@link EntityManager}
     */
    protected EntityManager getEntityManager() {
        return this.entityManager;
    }

    /**
     * @return the type of the class to be used in the db operations.
     */
    protected abstract Class<T> getPersistentClass();

    /**
     * Persists an object.
     *
     * @param object the object to be persisted
     * @return the object
     */
    public T add(final T object) {
        this.getEntityManager().persist(object);
        return object;
    }

    /**
     * Updates an object in the DB.
     *
     * @param object to be updated
     */
    public void update(final T object) {
        this.getEntityManager().merge(object);
    }

    /**
     * Removes an object from DB.
     *
     * @param id the id of the object to be removed
     */
    public void remove(final ID id) {
        T object = this.findById(id);
        if (object != null) {
            this.getEntityManager().remove(object);
        }
    }

    /**
     * Finds an object by its id.
     *
     * @param id the id to be used to find the object.
     * @return The object found or null
     */
    public T findById(final ID id) {
        return this.getEntityManager().find(this.getPersistentClass(), id);
    }

    /**
     * @return All objects from DB
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return this.getEntityManager().createQuery("FROM " + this.getPersistentClass().getSimpleName() + " o")
                .getResultList();
    }

    /**
     * @return a counter with the number of objects in the db
     */
    public int countAll() {
        Query query = this.getEntityManager().createQuery(
                "SELECT COUNT(o) FROM " + this.getPersistentClass().getSimpleName() + " o");
        return ((Long) query.getSingleResult()).intValue();
    }

    /**
     * Finds a single result based on a {@link GenericFilter}.
     *
     * @param filter
     *            The filter used in the search.
     * @return The result or null.
     */
    @SuppressWarnings("unchecked")
    public T findSingleResultByGenericFilter(final GenericFilter filter) {
        StringBuilder jpql = new StringBuilder("FROM " + this.getPersistentClass().getSimpleName() + " o");
        jpql.append(this.createConditionsFromFilter(filter));

        Query query = this.getEntityManager().createQuery(jpql.toString());
        this.applyFilterToQuery(query, filter);

        try {
            return (T) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Checks whether there is a record that meets the parameters defined in the {@code filter}.
     *
     * @param filter
     *            The {@link GenericFilter}
     * @return a boolean indicating if there is a record or not meeting the parameters.
     */
    public boolean existsByGenericFilter(final GenericFilter filter) {
        StringBuilder jpql = new StringBuilder("SELECT 1 FROM " + this.getPersistentClass().getSimpleName() + " o");
        jpql.append(this.createConditionsFromFilter(filter));

        Query query = this.getEntityManager().createQuery(jpql.toString());
        this.applyFilterToQuery(query, filter);
        query.setMaxResults(1);

        return query.getResultList().size() > 0;
    }

    /**
     * Finds a list of objects based on a {@link GenericFilter}.
     *
     * @param filter
     *            The filter used in the search.
     * @return The list containing the results or an empty list if none was found.
     */
    @SuppressWarnings("unchecked")
    public List<T> findByGenericFilter(final GenericFilter filter) {
        StringBuilder jpql = new StringBuilder("FROM " + this.getPersistentClass().getSimpleName() + " o");
        jpql.append(this.createConditionsFromFilter(filter));
        jpql.append(this.createOrderByFromFilter(filter));

        Query query = this.getEntityManager().createQuery(jpql.toString());
        this.applyFilterToQuery(query, filter);

        return query.getResultList();
    }

    /**
     * Finds a list of objects based on a select command and on a {@link GenericFilter}.
     *
     * @param select
     *            The select used in the beginning of the query
     * @param filter
     *            The filter used in the search.
     * @return The list containing the results or an empty list if none was found.
     */
    @SuppressWarnings("unchecked")
    public List<T> findByGenericFilter(final String select, final GenericFilter filter) {
        StringBuilder jpql = new StringBuilder(select);
        jpql.append(this.createConditionsFromFilter(filter));
        jpql.append(this.createOrderByFromFilter(filter));

        Query query = this.getEntityManager().createQuery(jpql.toString());
        this.applyFilterToQuery(query, filter);

        return query.getResultList();
    }

    /**
     * Creates a string containing the query conditions (WHERE, AND) based on the {@code filter}.
     *
     * @param filter
     *            The filter used in the search
     * @return The string containing the conditions
     */
    private String createConditionsFromFilter(final GenericFilter filter) {
        StringBuilder conditions = new StringBuilder();

        boolean firstParam = true;
        for (String paramName : filter.getParamsNames()) {
            if (firstParam) {
                conditions.append(" WHERE ");
                firstParam = false;
            } else {
                conditions.append(" AND ");
            }

            // For now, it is only supported = operator..
            conditions.append(paramName).append(" = :").append(this.removePoint(paramName));
        }

        return conditions.toString();
    }

    /**
     * Applies the {@code filter} to the {@code query}.
     *
     * @param query
     *            The query to receive the filter
     * @param filter
     *            The filter to be applied
     */
    private void applyFilterToQuery(final Query query, final GenericFilter filter) {
        for (String paramName : filter.getParamsNames()) {
            query.setParameter(this.removePoint(paramName), filter.getParamValue(paramName));
        }
    }

    /**
     * Creates a String containing the order clause based on the {@code filter}.
     *
     * @param filter
     *            The filter used to build the order clause
     * @return The String with the order clause or an empty String if there is no order field in the filter
     */
    private String createOrderByFromFilter(final GenericFilter filter) {
        if (StringUtils.isBlank(filter.getOrderField())) {
            return "";
        }
        return " ORDER BY " + filter.getOrderField();
    }

    /**
     * Remove the "." character from the given String.
     *
     * @param paramName
     *            The name of the parameter
     * @return The string withou the "." character
     */
    private String removePoint(final String paramName) {
        return paramName.replaceAll("\\.", "");
    }
}
