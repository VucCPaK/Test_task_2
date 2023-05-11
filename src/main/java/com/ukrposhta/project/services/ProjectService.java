package com.ukrposhta.project.services;

import com.ukrposhta.project.entities.Manager;
import com.ukrposhta.project.entities.Programmer;
import com.ukrposhta.project.entities.Project;
import com.ukrposhta.project.exceptions.ProjectNotFoundException;
import com.ukrposhta.project.repositories.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProgrammerService programmerService;
    private final ManagerService managerService;

    public Project getProjectById(String id) {
        return projectRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ProjectNotFoundException(id));
    }

    public Set<Manager> getManagersByProjectId(String id) {
        var project = projectRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ProjectNotFoundException(id));
        return project.getManagers();
    }

    public Set<Programmer> getProgrammersByProjectId(String id) {
        var project = projectRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ProjectNotFoundException(id));
        return project.getProgrammers();
    }

    public String createProject(String projectName) {
        var project = Project.builder()
                .id(UUID.randomUUID())
                .projectName(projectName)
                .managers(new HashSet<>())
                .programmers(new HashSet<>())
                .build();

        var savedProject = projectRepository.save(project);
        log.info("Created project with id: {}", savedProject.getId());
        return savedProject.getId().toString();
    }

    public void deleteProjectById(String id) {
        var project = projectRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ProjectNotFoundException(id));

        project.getProgrammers().forEach(programmer -> programmer.getProjects().remove(project));
        project.getManagers().forEach(manager -> manager.getProjects().remove(project));

        projectRepository.deleteById(UUID.fromString(id));
        log.info("Deleted project with id: {}", id);
    }

    public void addManagerToProject(String projectId, String managerId) {
        managerService.addProject(managerId, projectId);
    }

    public void removeManagerFromProject(String projectId, String managerId) {
        managerService.removeProject(managerId, projectId);
    }

    public void addProgrammersToProject(String projectId, String programmerId) {
        programmerService.addProject(programmerId, projectId);
    }

    public void removeProgrammersFromProject(String projectId, String programmerId) {
        programmerService.removeProject(programmerId, projectId);
    }

    public void changeNameOfProject(String id, String newName) {
        var project = projectRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ProjectNotFoundException(id));

        project.setProjectName(newName);

        projectRepository.save(project);
        log.info("New name: {} of project: {}", newName, id);
    }
}
