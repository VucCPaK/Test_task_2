package com.ukrposhta.project.exceptions;

public class ProgrammerNotFoundException extends RuntimeException {
    public ProgrammerNotFoundException(String id) {
        super("Could not find programmer with id: " + id);
    }
}
