package com.rxvlvxr.bank.daos;

import com.rxvlvxr.bank.models.User;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(readOnly = true)
public class UserDAO {
    private final EntityManager entityManager;

    @Autowired
    public UserDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<User> findAllByPhoneNumber(String number) {
        Session session = entityManager.unwrap(Session.class);

        return session.createSelectionQuery("from User u join Phone p on u.id=p.user.id where p.number= :number", User.class).setParameter("number", number).getResultList();
    }

    public List<User> findAllByEmail(String email) {
        Session session = entityManager.unwrap(Session.class);

        return session.createSelectionQuery("from User u join Email e on u.id=e.user.id where e.address= :email", User.class).setParameter("email", email).getResultList();
    }
}
