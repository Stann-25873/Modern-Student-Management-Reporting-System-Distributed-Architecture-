// File: StudentRegistrationSystemServer/src/com/server/service/ProgramServiceRemoteImpl.java
package com.server.service;

import com.server.dao.ProgramDAO;
import com.server.model.Program;
import com.server.remote.ProgramServiceRemote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ProgramServiceRemoteImpl extends UnicastRemoteObject implements ProgramServiceRemote {

    private final ProgramDAO programDAO;

    public ProgramServiceRemoteImpl() throws RemoteException {
        super();
        this.programDAO = new ProgramDAO();
    }
    
    private void validateProgram(Program program) throws RemoteException {
        if (program.getName() == null || program.getName().trim().isEmpty()) {
            throw new RemoteException("Program name cannot be empty.");
        }
        if (program.getDurationYears() == null || program.getDurationYears() <= 0) {
            throw new RemoteException("Program duration must be a valid positive number.");
        }
        if (program.getDepartment() == null || program.getDepartment().getId() == null) {
            throw new RemoteException("Department is mandatory for a program.");
        }
    }

    @Override
    public void addProgram(Program program) throws RemoteException {
        try {
            validateProgram(program);
            programDAO.save(program);
        } catch (Exception e) {
            throw new RemoteException("Server error saving program.", e);
        }
    }

    @Override
    public Program getProgramById(Long programId) throws RemoteException {
        try {
            return programDAO.findById(programId);
        } catch (Exception e) {
            throw new RemoteException("Error finding program by ID.", e);
        }
    }

    @Override
    public List<Program> getAllPrograms() throws RemoteException {
       ProgramDAO dao = new ProgramDAO(); // Ensure it uses the specific DAO
    return dao.findAll();
    }

    @Override
    public void updateProgram(Program program) throws RemoteException {
        try {
            if (program.getId() == null || program.getId() <= 0) {
                 throw new RemoteException("Program ID is required for update.");
            }
            validateProgram(program);
            programDAO.update(program);
        } catch (Exception e) {
            throw new RemoteException("Server error updating program.", e);
        }
    }

    @Override
    public void deleteProgram(Long programId) throws RemoteException {
        try {
            Program program = programDAO.findById(programId);
            if (program != null) {
                programDAO.delete(program);
            } else {
                throw new RemoteException("Program ID " + programId + " not found.");
            }
        } catch (Exception e) {
            throw new RemoteException("Server error deleting program.", e);
        }
    }

    @Override
    public List<Program> getProgramsByDepartment(Long departmentId) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}