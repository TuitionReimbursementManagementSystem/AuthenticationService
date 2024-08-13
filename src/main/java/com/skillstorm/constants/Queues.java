package com.skillstorm.constants;

public enum Queues {

    // From AuthUser-Service:
    REGISTRATION_REQUEST("registration-request-queue"),

    // To AuthUser-Service:
    REGISTRATION_RESPONSE("registration-response-queue");

    private final String queue;

    Queues(String queue) {
        this.queue = queue;
    }

    @Override
    public String toString() {
        return queue;
    }
}
