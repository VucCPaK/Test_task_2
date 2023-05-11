package com.ukrposhta.project.services;

import com.ukrposhta.project.entities.*;
import com.ukrposhta.project.enums.ProgrammerType;
import com.ukrposhta.project.enums.SkillLevel;
import com.ukrposhta.project.exceptions.ProgrammerNotFoundException;
import com.ukrposhta.project.exceptions.ProjectNotFoundException;
import com.ukrposhta.project.repositories.ProgrammerRepository;
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
public class ProgrammerService {

    private final ProgrammerRepository programmerRepository;
    private final ProjectRepository projectRepository;

    public Programmer getProgrammerById(String id) {
        return programmerRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ProgrammerNotFoundException(id));
    }

    public Set<Project> getProjectsByProgrammerId(String id) {
        var programmer = programmerRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ProgrammerNotFoundException(id));
        return programmer.getProjects();
    }

    public String createProgrammer(String name, SkillLevel skillLevel,
                                   ProgrammerType programmerType) {

        var programmer = Programmer.builder()
                .id(UUID.randomUUID())
                .name(name)
                .skillLevel(skillLevel.name())
                .type(programmerType.name())
                .projects(new HashSet<>())
                .build();

        var savedProgrammer = programmerRepository.save(programmer);
        log.info("Created programmer with programmerId: {}", programmer.getId());
        return savedProgrammer.getId().toString();
    }

    public void deleteProgrammerById(String id) {
        var programmer = programmerRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ProgrammerNotFoundException(id));

        programmer.getProjects().forEach(project -> project.getProgrammers().remove(programmer));

        programmerRepository.deleteById(UUID.fromString(id));
        log.info("Deleted programmer with programmerId: {}", id);
    }

    public void addProject(String programmerId, String projectId) {
        var programmer = programmerRepository.findById(
                UUID.fromString(programmerId)).orElseThrow(() -> new ProgrammerNotFoundException(programmerId));
        var project = projectRepository.findById(
                UUID.fromString(projectId)).orElseThrow(() -> new ProjectNotFoundException(projectId));

        programmer.addProject(project);

        programmerRepository.save(programmer);

        log.info("Added project: {} to programmer: {}", projectId, programmerId);
    }

    public void removeProject(String programmerId, String projectId) {
        var programmer = programmerRepository.findById(
                UUID.fromString(programmerId)).orElseThrow(() -> new ProgrammerNotFoundException(programmerId));
        var project = projectRepository.findById(
                UUID.fromString(projectId)).orElseThrow(() -> new ProjectNotFoundException(projectId));

        programmer.removeProject(project);

        programmerRepository.save(programmer);

        log.info("Removed project: {} from programmer: {}", projectId, programmerId);
    }

    public void changeName(String id, String newName) {
        var programmer = programmerRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ProgrammerNotFoundException(id));

        programmer.setName(newName);

        programmerRepository.save(programmer);
        log.info("New name: {} of programmer: {}", newName, id);
    }

    public void changeSkillLevel(String id, SkillLevel skillLevel) {
        var programmer = programmerRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ProgrammerNotFoundException(id));

        programmer.setSkillLevel(skillLevel.name());

        programmerRepository.save(programmer);
        log.info("New skill level: {} of programmer: {}", skillLevel.name(), id);
    }

    public void changeProgrammerType(String id, ProgrammerType programmerType) {
        var programmer = programmerRepository.findById(UUID.fromString(id)).orElseThrow(() -> new ProgrammerNotFoundException(id));

        programmer.setSkillLevel(programmerType.name());

        programmerRepository.save(programmer);
        log.info("New type: {} of programmer: {}", programmerType.name(), id);
    }
}
