// File: StudentRegistrationSystemServer/src/com/server/remote/RegistrationDetailsServiceRemote.java
package com.server.remote;

import com.server.model.RegistrationDetails;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface RegistrationDetailsServiceRemote extends Remote {
    
    // Les opérations CRUD de base sont plus complètes
    void addDetails(RegistrationDetails details) throws RemoteException;
    RegistrationDetails getDetailsById(Long detailsId) throws RemoteException;
    List<RegistrationDetails> getAllDetails() throws RemoteException;
    void updateDetails(RegistrationDetails details) throws RemoteException;
    void deleteDetails(Long detailsId) throws RemoteException;
}