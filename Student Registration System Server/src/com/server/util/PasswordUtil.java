// File: StudentRegistrationSystemServer/src/com/server/util/PasswordUtil.java

package com.server.util;

// Importation pour la fonction de hachage (exemple avec une librairie externe comme jBCrypt)
// import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // --- Placeholder pour le hachage sécurisé (doit être remplacé par BCrypt) ---
    
    /**
     * Hashes the plain text password for secure storage.
     * TODO: Implement secure BCrypt hashing here.
     * WARNING: Using simple hashing for demonstration. REPLACE WITH BCrypt!
     */
    public static String hashPassword(String plainPassword) {
        // En production, utiliser: return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        return "HASH_" + plainPassword; // Placeholder simple
    }

    /**
     * Checks if a plain text password matches the hashed password from the database.
     * @return true if passwords match, false otherwise.
     */
    public static boolean checkPassword(String plainPassword, String storedHash) {
        // En production, utiliser: return BCrypt.checkpw(plainPassword, storedHash);
        return storedHash != null && storedHash.equals("HASH_" + plainPassword); // Placeholder simple
    }
}