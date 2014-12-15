package com.cpqd.vppd.alarmmanager.utils.repository;

import javax.persistence.EntityManager;
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
}
