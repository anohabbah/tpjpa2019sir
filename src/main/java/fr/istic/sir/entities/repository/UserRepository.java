package fr.istic.sir.entities.repository;

import fr.istic.sir.entities.User;
import fr.istic.sir.jpa.EntityManagerHelper;

import javax.persistence.Query;
import java.util.List;

public class UserRepository extends EntityRepository<User> {
    public List<User> findByEmail(String email) {
        Query query = EntityManagerHelper.getEntityManager()
                .createQuery("select u from User u where u.email like :email");
        query.setParameter("email", "%" + email + "%");

        return query.getResultList();
    }
}
