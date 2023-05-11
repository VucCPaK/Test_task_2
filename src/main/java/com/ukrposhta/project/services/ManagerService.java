package com.ukrposhta.project.services;

import com.ukrposhta.project.entities.Manager;
import com.ukrposhta.project.entities.Project;
import com.ukrposhta.project.exceptions.ManagerNotFoundException;
import com.ukrposhta.project.exceptions.ProjectNotFoundException;
import com.ukrposhta.project.repositories.ManagerRepository;
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
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final ProjectRepository projectRepository;

    public Manager getManagerById(String id) {
        return managerRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ManagerNotFoundException(id));
    }

    public Set<Project> getProjectsByManagerId(String id) {
        var manager = managerRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ManagerNotFoundException(id));
        return manager.getProjects();
    }

    public String createManager(String name) {
        var manager = Manager.builder()
                .id(UUID.randomUUID())
                .name(name)
                .projects(new HashSet<>())
                .build();

        var savedManager = managerRepository.save(manager);
        log.info("Created manager with id: {}", manager.getId());
        return savedManager.getId().toString();
    }

    public void deleteManagerById(String id) {
        var manager = managerRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ManagerNotFoundException(id));

        manager.getProjects().forEach(project -> project.getManagers().remove(manager));

        managerRepository.deleteById(UUID.fromString(id));
        log.info("Deleted manager with id: {}", id);
    }

    public void addProject(String managerId, String projectId) {
        var manager = managerRepository.findById(
                UUID.fromString(managerId)).orElseThrow(() -> new ManagerNotFoundException(managerId));
        var project = projectRepository.findById(
                UUID.fromString(projectId)).orElseThrow(() -> new ProjectNotFoundException(projectId));

        manager.addProject(project);

        managerRepository.save(manager);

        log.info("Added project: {} to manager: {}", projectId, managerId);
    }

    public void removeProject(String managerId, String projectId) {
        var manager = managerRepository.findById(
                UUID.fromString(managerId)).orElseThrow(() -> new ManagerNotFoundException(managerId));
        var project = projectRepository.findById(
                UUID.fromString(projectId)).orElseThrow(() -> new ProjectNotFoundException(projectId));

        manager.removeProject(project);

        managerRepository.save(manager);

        log.info("Removed project: {} from manager: {}", projectId, managerId);
    }

    public void changeName(String id, String newName) {
        var manager = managerRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ManagerNotFoundException(id));

        manager.setName(newName);

        managerRepository.save(manager);
        log.info("New name: {} of manager: {}", newName, id);
    }
}
