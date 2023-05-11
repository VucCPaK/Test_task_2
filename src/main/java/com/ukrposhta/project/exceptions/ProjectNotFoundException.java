package com.ukrposhta.project.exceptions;

public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(String id) {
        super("Could not find project with id: " + id);
    }
}
