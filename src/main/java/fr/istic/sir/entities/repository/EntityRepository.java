package fr.istic.sir.entities.repository;

import fr.istic.sir.jpa.EntityManagerHelper;
import fr.istic.sir.repositories.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @param <T>
 * @author Abbah Anoh
 */
@SuppressWarnings("unchecked")
public abstract class EntityRepository<T> implements Repository<T> {
    private Class<T> entityClass;

    EntityRepository() {
        ParameterizedType genericClass = (ParameterizedType) getClass().getGenericSuperclass();
        entityClass = (Class<T>) genericClass.getActualTypeArguments()[0];
    }

    /**
     * Save a resource.
     *
     * @param o Resource to save
     * @return Return saved resource
     */
    @Override
    public T save(T o) {
        Objects.requireNonNull(o, "Parameter o passed to "
                + EntityRepository.class.getName() + "#save method should not be null.");

        runInTransaction(em -> em.persist(o));

        return o;
    }

    /**
     * Return all resources.
     *
     * @return Return all resources from database.
     */
    @Override
    public List<T> findAll() {
        Query query = EntityManagerHelper.getEntityManager()
                .createQuery("SELECT u FROM " + entityClass.getName() + " u");

        return query.getResultList();
    }

    /**
     * Find a single resource.
     *
     * @param columnName Column that search on.
     * @param value      Research value.
     * @return Return a list of resource.
     */
    @Override
    public List<T> findBy(String columnName, String value) {
        Objects.requireNonNull(columnName, "columnName should not be null.");
        Objects.requireNonNull(value, "value should not be null.");

        Query query = EntityManagerHelper.getEntityManager().createQuery("SELECT u FROM " + entityClass.getName() + " u WHERE " + columnName + " = :value");
        query.setParameter(value, value);

        return query.getResultList();
    }

    /**
     * Find a resource by it identity.
     *
     * @param id Resource identity.
     * @return Return a resource if the given identity exist, null if not.
     */
    @Override
    public Optional<T> findById(Object id) {
        Objects.requireNonNull(id, "id should not be null.");

        return Optional.ofNullable(EntityManagerHelper.getEntityManager().find(entityClass, id));
    }

    /**
     * Find a resource base on a column
     *
     * @param columnName Column that search on.
     * @param value      Research value.
     * @return Return a resource
     */
    @Override
    public Optional<T> findOne(String columnName, String value) {
        Objects.requireNonNull(columnName, "columnName should not be null.");
        Objects.requireNonNull(value, "value should not be null.");

        Query query = EntityManagerHelper.getEntityManager()
                .createQuery("SELECT u FROM " + entityClass.getName() + " u WHERE " + columnName + " = :value");
        query.setParameter(value, value);

        return Optional.ofNullable((T) query.getSingleResult());
    }

    /**
     * Update a resource.
     *
     * @param o Resource to update
     * @return Return updated resource.
     */
    @Override
    public T update(T o) {
        Objects.requireNonNull(o, "Parameter o passed to "
                + EntityRepository.class.getName() + "#update method should not be null.");

        runInTransaction(em -> em.merge(o));
        return o;
    }

    /**
     * Delete a resource
     *
     * @param o Resource to delete.
     * @return Return deleted resource
     */
    @Override
    public T delete(T o) {
        Objects.requireNonNull(o, "Parameter o passed to "
                + EntityRepository.class.getName() + "#delete method should not be null.");

        runInTransaction(em -> em.remove(o));

        return o;
    }

    /**
     * Run a query in a transaction try catch.
     *
     * @param action Action query to run
     */
    public void runInTransaction(Consumer<EntityManager> action) {
        EntityManagerHelper.beginTransaction();
        try {
            action.accept(EntityManagerHelper.getEntityManager());
        } catch (Exception e) {
            EntityManagerHelper.rollback();
            e.printStackTrace();
        }
        EntityManagerHelper.commit();

        EntityManagerHelper.closeEntityManager();
//        EntityManagerHelper.closeEntityManagerFactory();
    }
}
