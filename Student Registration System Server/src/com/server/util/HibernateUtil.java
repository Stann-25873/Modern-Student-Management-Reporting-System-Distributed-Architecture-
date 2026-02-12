// File: StudentRegistrationSystemServer/src/com/server/util/HibernateUtil.java

package com.server.util;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Créer la SessionFactory à partir de hibernate.cfg.xml
            // Utilise la classe org.hibernate.cfg.Configuration pour la version 4.3.x
            return new Configuration().configure().buildSessionFactory();
        } catch (HibernateException ex) {
            // Afficher l'erreur lors de l'initialisation de la SessionFactory
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Fermer les caches et les pools de connexions
        getSessionFactory().close();
    }
}