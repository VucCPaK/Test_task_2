package com.ukrposhta.project.controllers;

import com.ukrposhta.project.dto.*;
import com.ukrposhta.project.entities.Manager;
import com.ukrposhta.project.entities.Project;
import com.ukrposhta.project.services.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    @GetMapping("/{managerId}")
    public ResponseEntity<Manager> getManagerById(@PathVariable String managerId) {
        return ResponseEntity.ok(managerService.getManagerById(managerId));
    }

    @GetMapping("/projects/{managerId}")
    public ResponseEntity<Set<Project>> getProjectsByManagerId(@PathVariable String managerId) {
        return ResponseEntity.ok(managerService.getProjectsByManagerId(managerId));
    }

    @PostMapping("/new")
    public ResponseEntity<String> createManager(@RequestBody CreateManagerDTO createManagerDTO) {
        String id = managerService.createManager(
                createManagerDTO.name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @DeleteMapping("/{managerId}")
    public ResponseEntity<Void> deleteManagerById(@PathVariable String managerId) {
        managerService.deleteManagerById(managerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/projects/add")
    public ResponseEntity<Void> addProjectsToManager(@RequestBody UpdateProjectsDTO updateProjectsDTO) {
        managerService.addProject(updateProjectsDTO.id(), updateProjectsDTO.projectId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/projects/remove")
    public ResponseEntity<Void> removeProjectsFromManager(@RequestBody UpdateProjectsDTO updateProjectsDTO) {
        managerService.removeProject(updateProjectsDTO.id(), updateProjectsDTO.projectId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/name")
    public ResponseEntity<Void> changeNameOfManager(@RequestBody ChangeManagerNameDTO changeManagerNameDTO) {
        managerService.changeName(changeManagerNameDTO.id(), changeManagerNameDTO.newName());
        return ResponseEntity.ok().build();
    }
}

