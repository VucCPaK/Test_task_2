package com.ukrposhta.project.dto;

import com.ukrposhta.project.enums.ProgrammerType;

public record ChangeProgrammerTypeDTO(String programmerId, ProgrammerType type) {
}
