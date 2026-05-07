package com.sams.util;

import java.net.URL;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            URL configurationUrl = HibernateUtil.class.getClassLoader().getResource("hibernate.cfg.xml");
            if (configurationUrl == null) {
                throw new IllegalStateException("Missing hibernate.cfg.xml on the runtime classpath");
            }
            return new Configuration().configure(configurationUrl).buildSessionFactory();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to create SessionFactory", ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}
