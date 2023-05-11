package com.ukrposhta.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukrposhta.project.dto.*;
import com.ukrposhta.project.entities.Manager;
import com.ukrposhta.project.entities.Programmer;
import com.ukrposhta.project.entities.Project;
import com.ukrposhta.project.enums.ProgrammerType;
import com.ukrposhta.project.enums.SkillLevel;
import com.ukrposhta.project.repositories.ManagerRepository;
import com.ukrposhta.project.repositories.ProgrammerRepository;
import com.ukrposhta.project.repositories.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProgrammerRepository programmerRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    ObjectMapper objectMapper;

    private UUID programmerId;
    private Programmer programmer;
    private UUID projectId;
    private Project project;
    private UUID managerId;
    private Manager manager;

    @BeforeEach
    public void initEach() {
        managerId = UUID.randomUUID();
        manager = Manager.builder().id(managerId).name("Alex")
                .projects(new HashSet<>()).build();

        programmerId = UUID.randomUUID();
        programmer = Programmer.builder().id(programmerId).name("Antony").skillLevel(SkillLevel.MIDDLE.name())
                .type(ProgrammerType.DEVELOPER.name()).projects(new HashSet<>()).build();

        projectId = UUID.randomUUID();
        project = Project.builder().id(projectId).projectName("ukrposhta")
                .managers(new HashSet<>()).programmers(new HashSet<>()).build();
    }


    @Test
    public void mustCreateNewProjectAndReturnStringAsAnId() throws Exception {

        var createProjectDTO = new CreateProjectDTO("ukrposhta");

        mockMvc.perform(post("/api/v1/project/new")
                        .content(objectMapper.writeValueAsString(createProjectDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isString());
    }

    @Test
    public void mustReturnProjectWithCertainIdAndProjectNameIsUkrposhta() throws Exception {

        projectRepository.save(project);

        mockMvc.perform(get("/api/v1/project/{id}", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.projectName").value("ukrposhta"));
    }

    @Test
    public void mustReturnProjectNotFoundException() throws Exception {

        mockMvc.perform(get("/api/v1/project/{id}", UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data doesn't match"));
    }

    @Test
    public void mustReturnAllManagersThatOwnToProject() throws Exception {

        var savedProject = projectRepository.save(project);
        var savedManager = managerRepository.save(manager);

        savedManager.addProject(savedProject);
        managerRepository.save(savedManager);

        mockMvc.perform(get("/api/v1/manager/projects/{id}", managerId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void mustReturnAllProgrammersThatOwnToProject() throws Exception {

        var savedProject = projectRepository.save(project);
        var savedProgrammer = programmerRepository.save(programmer);

        savedProgrammer.addProject(savedProject);
        programmerRepository.save(savedProgrammer);

        mockMvc.perform(get("/api/v1/programmer/projects/{id}", programmerId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void mustDeleteProject() throws Exception {

        projectRepository.save(project);

        mockMvc.perform(delete("/api/v1/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void mustDeleteProjectAndDeleteProjectInAllProgrammersAndManagers() throws Exception {

        var savedProject = projectRepository.save(project);
        var savedProgrammer = programmerRepository.save(programmer);
        var savedManager = managerRepository.save(manager);

        savedManager.addProject(savedProject);
        savedProgrammer.addProject(savedProject);

        programmerRepository.save(savedProgrammer);
        managerRepository.save(savedManager);

        mockMvc.perform(delete("/api/v1/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/programmer/projects/{id}", programmerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty()); // array must be empty

        mockMvc.perform(get("/api/v1/manager/projects/{id}", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty()); // array must be empty
    }

    @Test
    public void mustChangeNameUkrposhtaToShop() throws Exception {

        projectRepository.save(project);

        var changeProjectNameDTO = new ChangeProjectNameDTO(projectId.toString(), "shop");

        mockMvc.perform(post("/api/v1/project/name")
                        .content(objectMapper.writeValueAsString(changeProjectNameDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/project/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId.toString()))
                .andExpect(jsonPath("$.projectName").value("shop"));
    }

    @Test
    public void mustAddManagerToProject() throws Exception {

        projectRepository.save(project);
        managerRepository.save(manager);

        var updateManagersDTO = new UpdateManagersDTO(projectId.toString(), managerId.toString());

        mockMvc.perform(post("/api/v1/project/managers/add")
                        .content(objectMapper.writeValueAsString(updateManagersDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/manager/projects/{id}", managerId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void mustRemoveManagerFromProject() throws Exception {

        var savedProject = projectRepository.save(project);
        var savedManager =  managerRepository.save(manager);

        savedManager.addProject(savedProject);
        managerRepository.save(savedManager);

        var updateManagersDTO = new UpdateManagersDTO(projectId.toString(), managerId.toString());

        mockMvc.perform(post("/api/v1/project/managers/remove")
                        .content(objectMapper.writeValueAsString(updateManagersDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/manager/projects/{id}", managerId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    public void mustAddProgrammerToProject() throws Exception {

        projectRepository.save(project);
        programmerRepository.save(programmer);

        var updateProgrammersDTO = new UpdateProgrammersDTO(projectId.toString(), programmerId.toString());

        mockMvc.perform(post("/api/v1/project/programmers/add")
                        .content(objectMapper.writeValueAsString(updateProgrammersDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/programmer/projects/{id}", programmerId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void mustRemoveProgrammerFromProject() throws Exception {

        var savedProject = projectRepository.save(project);
        var savedProgrammer =  programmerRepository.save(programmer);

        savedProgrammer.addProject(savedProject);
        programmerRepository.save(savedProgrammer);

        var updateProgrammersDTO = new UpdateProgrammersDTO(projectId.toString(), programmerId.toString());

        mockMvc.perform(post("/api/v1/project/programmers/remove")
                        .content(objectMapper.writeValueAsString(updateProgrammersDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/programmer/projects/{id}", programmerId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$").isEmpty());
    }
}
