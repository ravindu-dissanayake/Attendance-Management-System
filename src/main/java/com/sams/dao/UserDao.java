package com.sams.dao;

import com.sams.entity.AppUser;
import com.sams.util.HibernateUtil;
import org.hibernate.Session;

import java.util.Optional;

public class UserDao extends AbstractDao<AppUser> {
    public UserDao() {
        super(AppUser.class);
    }

    public Optional<AppUser> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            AppUser user = session.createQuery("from AppUser u where u.username = :username", AppUser.class)
                    .setParameter("username", username)
                    .uniqueResult();
            return Optional.ofNullable(user);
        }
    }
}
