package com.ukrposhta.project.dto;

import com.ukrposhta.project.enums.ProgrammerType;
import com.ukrposhta.project.enums.SkillLevel;

public record CreateProgrammerDTO(String name, SkillLevel skillLevel, ProgrammerType programmerType){
}
