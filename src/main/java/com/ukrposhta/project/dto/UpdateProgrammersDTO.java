package com.ukrposhta.project.dto;

import com.ukrposhta.project.entities.Programmer;

import java.util.Set;

public record AddProgrammersDTO(String projectId, Set<Programmer> programmers) {
}
