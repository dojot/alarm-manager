package com.cpqd.vppd.alarmmanager.utils.repository;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.Hibernate;

/**
 * Abstract base class for entities.
 * @param <T> the type
 */
@MappedSuperclass
public abstract class BaseEntity<T extends BaseEntity<T>> {

    private int _version;

    /**
     * Default.
     */
    protected BaseEntity() {
        // default
    }

    // optimistic locking version, private since only Hibernate uses these
    @Version
    @Column(name = "oplock", nullable = false)
    @SuppressWarnings("unused")
    private int getVersion() {
        return _version;
    }

    @SuppressWarnings("unused")
    private void setVersion(final int version) {
        _version = version;
    }

    /**
     * Utility method for <code>equals()</code> methods.
     *
     * @param o1 one object
     * @param o2 another object
     * @return <code>true</code> if they're both <code>null</code> or both equal
     */
    protected boolean areEqual(final Object o1, final Object o2) {
        if (o1 == null) {
            if (o2 != null) {
                return false;
            }
        } else if (!o1.equals(o2)) {
            return false;
        }

        return true;
    }

    /**
     * Utility method for <code>equals()</code> methods.
     *
     * @param s1         one string
     * @param s2         another string
     * @param ignoreCase if <code>false</code> do case-sensitive comparison
     * @return <code>true</code> if they're both <code>null</code> or both equal
     */
    protected boolean areEqual(
            final String s1,
            final String s2,
            final boolean ignoreCase) {
        // for use in custom equals() methods

        if (s1 == null && s2 == null) {
            return true;
        }

        if (s1 == null || s2 == null) {
            return false;
        }

        return ignoreCase ? s1.equalsIgnoreCase(s2) : s1.equals(s2);
    }

    /**
     * Utility method for <code>equals()</code> methods.
     *
     * @param d1 one date
     * @param d2 another date
     * @return <code>true</code> if they're both <code>null</code> or both equal
     */
    protected boolean areEqual(
            final Date d1,
            final Date d2) {
        // for use in custom equals() methods

        if (d1 == null && d2 == null) {
            return true;
        }

        if (d1 == null || d2 == null) {
            return false;
        }

        return d1.getTime() == d2.getTime();
    }

    /**
     * Utility method for <code>equals()</code> methods.
     *
     * @param f1 one float
     * @param f2 another float
     * @return <code>true</code> if they're equal
     */
    protected boolean areEqual(
            final float f1,
            final float f2) {
        // for use in custom equals() methods

        return Float.floatToIntBits(f1) == Float.floatToIntBits(f2);
    }

    /**
     * Utility method for <code>hashCode()</code> methods.
     *
     * @param values the values to use in calculation
     * @return the hash code value
     */
    protected int calculateHashCode(final Object... values) {
        HashCodeBuilder builder = new HashCodeBuilder();
        for (Object value : values) {
            builder.append(value);
        }
        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return calculateHashCode(getHashCodeData());
    }

    /**
     * Get the data used to calculate hash code;
     * use getters not fields in case the instance is a proxy.
     *
     * @return the data
     */
    @Transient
    protected abstract Object[] getHashCodeData();

    /**
     * Allows type-specific "this".
     *
     * @return this
     */
    @Transient
    protected abstract T getThis();

    /**
     * Get the primary key.
     *
     * @return the pk, or <code>null</code> if not set or if not supported
     */
    @Transient
    public abstract Serializable getPk();

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (other == null
                || // looks into the target class of a proxy if necessary
                !Hibernate.getClass(other).equals(Hibernate.getClass(this))) {
            return false;
        }

        // if pks are both set, compare
        if (getPk() != null) {
            Serializable otherPk = ((BaseEntity) other).getPk();
            if (otherPk != null) {
                return getPk().equals(otherPk);
            }
        }

        return dataEquals((T) other);
    }

    /**
     * Compare data only; null, class, and pk have been checked.
     *
     * @param other the other instance
     * @return <code>true</code> if equal
     */
    protected abstract boolean dataEquals(T other);
}
