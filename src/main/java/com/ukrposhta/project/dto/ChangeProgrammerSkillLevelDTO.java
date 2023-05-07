package com.ukrposhta.project.dto;

import com.ukrposhta.project.enums.SkillLevel;

public record ChangeSkillLevelDTO(String programmerId, SkillLevel skillLevel) {
}
