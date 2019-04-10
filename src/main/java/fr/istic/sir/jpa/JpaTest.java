package fr.istic.sir.jpa;

import com.github.javafaker.Faker;
import fr.istic.sir.entities.User;
import fr.istic.sir.entities.repository.UserRepository;
import fr.istic.sir.repositories.Repository;

public class JpaTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Repository<User> rep = new UserRepository();

        Faker faker = new Faker();
        for (int i = 0; i < 10; ++i) {
            User user = new User(faker.internet().emailAddress(), faker.name().lastName(), faker.name().firstName());
            rep.save(user);
            System.out.println(i);
        }
    }
}
