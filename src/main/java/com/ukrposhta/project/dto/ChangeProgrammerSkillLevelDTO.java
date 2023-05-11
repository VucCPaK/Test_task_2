package com.ukrposhta.project.dto;

import com.ukrposhta.project.enums.SkillLevel;

public record ChangeProgrammerSkillLevelDTO(String programmerId, SkillLevel skillLevel) {
}
