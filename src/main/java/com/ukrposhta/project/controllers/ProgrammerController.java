package com.ukrposhta.project.controllers;

import com.ukrposhta.project.dto.*;
import com.ukrposhta.project.entities.Programmer;
import com.ukrposhta.project.entities.Project;
import com.ukrposhta.project.services.ProgrammerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/programmer")
@RequiredArgsConstructor
public class ProgrammerController {

    private final ProgrammerService programmerService;

    @GetMapping("/{programmerId}")
    public ResponseEntity<Programmer> getProgrammerById(@PathVariable String programmerId) {
        return ResponseEntity.ok(programmerService.getProgrammerById(programmerId));
    }

    @GetMapping("/projects/{programmerId}")
    public ResponseEntity<Set<Project>> getProjectsByProgrammerId(@PathVariable String programmerId) {
        return ResponseEntity.ok(programmerService.getProjectsByProgrammerId(programmerId));
    }

    @PostMapping("/new")
    public ResponseEntity<String> createProgrammer(@RequestBody CreateProgrammerDTO createProgrammerDTO) {
        String id = programmerService.createProgrammer(
                createProgrammerDTO.name(),
                createProgrammerDTO.skillLevel(),
                createProgrammerDTO.programmerType()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @DeleteMapping("/{programmerId}")
    public ResponseEntity<Void> deleteProgrammerById(@PathVariable String programmerId) {
        programmerService.deleteProgrammerById(programmerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/projects/add")
    public ResponseEntity<Void> addProjectsToProgrammer(@RequestBody UpdateProjectsDTO updateProjectsDTO) {
        programmerService.addProject(updateProjectsDTO.id(), updateProjectsDTO.projectId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/projects/remove")
    public ResponseEntity<Void> removeProjectsFromProgrammer(@RequestBody UpdateProjectsDTO updateProjectsDTO) {
        programmerService.removeProject(updateProjectsDTO.id(), updateProjectsDTO.projectId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/name")
    public ResponseEntity<Void> changeNameOfProgrammer(@RequestBody ChangeProgrammerNameDTO changeProgrammerNameDTO) {
        programmerService.changeName(changeProgrammerNameDTO.id(), changeProgrammerNameDTO.newName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/skill")
    public ResponseEntity<Void> changeSkillLevelOfProgrammer(@RequestBody ChangeProgrammerSkillLevelDTO changeProgrammerSkillLevelDto) {
        programmerService.changeSkillLevel(changeProgrammerSkillLevelDto.programmerId(), changeProgrammerSkillLevelDto.skillLevel());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/type")
    public ResponseEntity<Void> changeProgrammerTypeOfProgrammer(@RequestBody ChangeProgrammerTypeDTO changeProgrammerTypeDTO) {
        programmerService.changeProgrammerType(changeProgrammerTypeDTO.programmerId(), changeProgrammerTypeDTO.type());

        return ResponseEntity.ok().build();
    }
}
