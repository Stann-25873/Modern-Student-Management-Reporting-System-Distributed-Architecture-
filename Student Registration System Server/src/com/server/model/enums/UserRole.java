package com.server.model.enums;

public enum UserRole {
    ADMIN("Administrator"),   // Peut gérer le système, les utilisateurs et le catalogue
    STAFF("Academic Staff"),  // Peut gérer les cours, les départements et les programmes
    STUDENT("Student");       // Peut s'inscrire aux cours et consulter ses notes

    private final String displayValue;

    UserRole(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}