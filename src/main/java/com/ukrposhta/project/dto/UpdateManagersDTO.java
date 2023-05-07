package com.ukrposhta.project.dto;

import com.ukrposhta.project.entities.Manager;

import java.util.Set;

public record AddManagersDTO(String projectId, Set<Manager> managers) {
}
