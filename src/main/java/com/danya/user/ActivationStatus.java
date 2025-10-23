package com.danya.user;

public enum ActivationStatus {
    ACTIVE, DISABLED;

    public boolean isActive() {
        return this == ACTIVE;
    }

}
