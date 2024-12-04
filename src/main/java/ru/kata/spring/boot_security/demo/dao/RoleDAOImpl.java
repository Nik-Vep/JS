package ru.kata.spring.boot_security.demo.dao;

import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.Role;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class RoleDAOImpl implements RoleDAO{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Role> allRoles() {
        TypedQuery<Role> query = entityManager.createQuery("FROM Role", Role.class);
        return query.getResultList();
    }

    @Override
    public Role getById(Long id) {
        return entityManager.find(Role.class, id);
    }

}
