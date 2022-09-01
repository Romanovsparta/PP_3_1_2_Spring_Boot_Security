package ru.kata.spring.boot_security.demo.dao;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO {
    @PersistenceContext
    private EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    public UserDAOImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return entityManager.createQuery("SELECT user FROM User user", User.class).getResultList();
    }

    public User getUser(int id) {
        return (User) entityManager.createQuery("FROM User WHERE id = :id").setParameter("id", id).getSingleResult();
    }

    public User getUserByName(String name) {
        return (User) entityManager.createQuery("FROM User user JOIN FETCH user.roles WHERE username = :username").setParameter("username", name).getSingleResult();
    }

    public void save(User user) {
        entityManager.persist(user);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() != null) {
            user.setRoles(listRoles(user));
        }
    }

    public void update(int id, User upUser) {
        entityManager.merge(upUser);
        getUser(id).setPassword(passwordEncoder.encode(upUser.getPassword()));
        if (upUser.getRole() != null) {
            getUser(id).setRoles(listRoles(upUser));
        }
    }

    public void delete(int id) {
        entityManager.remove(getUser(id));
    }

    private List<Role> listRoles(User user) {
        List<Role> roles = new ArrayList<>();
        if (user.getRole().toLowerCase().contains("admin")) {
            roles.add(new Role(1, "ROLE_ADMIN"));
        }
        if (user.getRole().toLowerCase().contains("user")) {
            roles.add(new Role(2, "ROLE_USER"));
        }
        return roles;
    }
}
