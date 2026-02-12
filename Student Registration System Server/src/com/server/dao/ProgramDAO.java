// File: StudentRegistrationSystemServer/src/com/server/dao/ProgramDAO.java
package com.server.dao;

import com.server.model.Program;
import java.io.Serializable;

public class ProgramDAO extends BaseHibernateDAO<Program, Long> implements Serializable {

    private static final long serialVersionUID = 1L;

    public ProgramDAO() {
        super(Program.class);
        
        
    }
    
}
