package com.skillstorm.constants;

public enum Queues {

    // From User-Service:
    REGISTRATION_REQUEST("registration-request-queue"),

    // To User-Service:
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
