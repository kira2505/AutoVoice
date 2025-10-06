package com.autovoice.enums;

public enum Role {

    MECHANIC("Mechanic"),
    ELECTRICIAN("Electrician"),
    MANAGER("Manager"),;

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
