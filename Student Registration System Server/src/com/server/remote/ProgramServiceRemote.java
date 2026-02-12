// File: StudentRegistrationSystemServer/src/com/server/remote/ProgramServiceRemote.java
package com.server.remote;

import com.server.model.Program;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ProgramServiceRemote extends Remote {
    
    String RMI_SERVICE_NAME = "ProgramService";

    void addProgram(Program program) throws RemoteException;
    Program getProgramById(Long programId) throws RemoteException;
    List<Program> getAllPrograms() throws RemoteException;
    List<Program> getProgramsByDepartment(Long departmentId) throws RemoteException;
    void updateProgram(Program program) throws RemoteException;
    void deleteProgram(Long programId) throws RemoteException;
}