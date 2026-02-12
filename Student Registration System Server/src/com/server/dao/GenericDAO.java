// File: StudentRegistrationSystemServer/src/com/server/dao/GenericDAO.java

package com.server.dao;

import java.util.List;

public interface GenericDAO<T, ID> {
    
    T save(T entity); // Sauvegarde et retourne l'entité
    T findById(ID id);
    List<T> findAll();
    void update(T entity);
    void delete(T entity);
    
    
}