package com.autovoice.enums;

public enum Branch {

    BRANCH_1("Branch 1"),
    BRANCH_2("Branch 2"),
    BRANCH_3("Branch 3"),
    BRANCH_4("Branch 4");

    private final String displayName;

    Branch(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
