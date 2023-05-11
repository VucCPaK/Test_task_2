package com.ukrposhta.project.controllers;

import com.ukrposhta.project.dto.*;
import com.ukrposhta.project.entities.Manager;
import com.ukrposhta.project.entities.Programmer;
import com.ukrposhta.project.entities.Project;
import com.ukrposhta.project.services.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{projectId}")
    public ResponseEntity<Project> getProjectById(@PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getProjectById(projectId));
    }

    @GetMapping("/managers/{projectId}")
    public ResponseEntity<Set<Manager>> getManagersByProjectId(@PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getManagersByProjectId(projectId));
    }

    @GetMapping("/programmers/{projectId}")
    public ResponseEntity<Set<Programmer>> getProgrammersByProjectId(@PathVariable String projectId) {
        return ResponseEntity.ok(projectService.getProgrammersByProjectId(projectId));
    }

    @PostMapping("/new")
    public ResponseEntity<String> createProject(@RequestBody CreateProjectDTO createProjectDTO) {
        String id = projectService.createProject(
                createProjectDTO.projectName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<Void> deleteProjectById(@PathVariable String projectId) {
        projectService.deleteProjectById(projectId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/managers/add")
    public ResponseEntity<Void> addManagersToProject(@RequestBody UpdateManagersDTO updateManagersDTO) {
        projectService.addManagerToProject(updateManagersDTO.projectId(), updateManagersDTO.managerId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/managers/remove")
    public ResponseEntity<Void> removeManagersFromProject(@RequestBody UpdateManagersDTO updateManagersDTO) {
        projectService.removeManagerFromProject(updateManagersDTO.projectId(), updateManagersDTO.managerId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/programmers/add")
    public ResponseEntity<Void> addProgrammersToProject(@RequestBody UpdateProgrammersDTO updateProgrammersDTO) {
        projectService.addProgrammersToProject(updateProgrammersDTO.projectId(), updateProgrammersDTO.programmerId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/programmers/remove")
    public ResponseEntity<Void> removeProgrammersFromProject(@RequestBody UpdateProgrammersDTO updateProgrammersDTO) {
        projectService.removeProgrammersFromProject(updateProgrammersDTO.projectId(),updateProgrammersDTO.programmerId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/name")
    public ResponseEntity<Void> changeNameOfProject(@RequestBody ChangeProjectNameDTO changeProjectNameDTO) {
        projectService.changeNameOfProject(changeProjectNameDTO.projectId(), changeProjectNameDTO.newName());
        return ResponseEntity.ok().build();
    }

}
