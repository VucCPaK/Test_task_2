package com.ukrposhta.project.exceptions;

public class ManagerNotFoundException extends RuntimeException {
    public ManagerNotFoundException(String id) {
        super("Could not find manager with id: " + id);
    }
}
