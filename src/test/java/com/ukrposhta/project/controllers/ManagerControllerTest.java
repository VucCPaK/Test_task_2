package com.ukrposhta.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukrposhta.project.dto.ChangeManagerNameDTO;
import com.ukrposhta.project.dto.CreateManagerDTO;
import com.ukrposhta.project.dto.UpdateProjectsDTO;
import com.ukrposhta.project.entities.Manager;
import com.ukrposhta.project.entities.Project;
import com.ukrposhta.project.repositories.ManagerRepository;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ManagerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    ObjectMapper objectMapper;

    private UUID managerId;
    private Manager manager;
    private UUID projectId;
    private Project project;

    @BeforeEach
    public void initEach() {
        managerId = UUID.randomUUID();
        manager = Manager.builder().id(managerId).name("Alex")
                .projects(new HashSet<>()).build();

        projectId = UUID.randomUUID();
        project = Project.builder().id(projectId).projectName("ukrposhta")
                .managers(new HashSet<>()).programmers(new HashSet<>()).build();
    }

    @Test
    public void mustCreateNewManagerAndReturnStringAsAnId() throws Exception {

        var createManagerDTO = new CreateManagerDTO("Alex");

        mockMvc.perform(post("/api/v1/manager/new")
                        .content(objectMapper.writeValueAsString(createManagerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isString());
    }

    @Test
    public void mustReturnManagerWithCertainIdAndNameIsAlex() throws Exception {

        managerRepository.save(manager);

        mockMvc.perform(get("/api/v1/manager/{id}", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(managerId.toString()))
                .andExpect(jsonPath("$.name").value("Alex"));
    }

    @Test
    public void mustReturnManagerNotFoundException() throws Exception {

        mockMvc.perform(get("/api/v1/manager/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data doesn't match"));
    }

    @Test
    public void mustReturnAllProjectsThatOwnToManager() throws Exception {

        var savedManager = managerRepository.save(manager);
        var savedProject = projectRepository.save(project);

        savedManager.addProject(savedProject);
        managerRepository.save(savedManager);

        mockMvc.perform(get("/api/v1/manager/projects/{id}", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void mustDeleteManager() throws Exception {

        managerRepository.save(manager);

        mockMvc.perform(delete("/api/v1/manager/{id}", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void mustDeleteManagerAndDeleteManagerInAllProjects() throws Exception {

        var savedManager = managerRepository.save(manager);
        var savedProject = projectRepository.save(project);

        savedManager.addProject(savedProject);
        managerRepository.save(savedManager);

        mockMvc.perform(delete("/api/v1/manager/{id}", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/project/managers/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty()); // array must be empty
    }

    @Test
    public void mustChangeNameAlexToMartin() throws Exception {

        managerRepository.save(manager);

        var changeManagerNameDTO = new ChangeManagerNameDTO(managerId.toString(), "Martin");

        mockMvc.perform(post("/api/v1/manager/name")
                        .content(objectMapper.writeValueAsString(changeManagerNameDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/manager/{id}", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(managerId.toString()))
                .andExpect(jsonPath("$.name").value("Martin"));
    }

    @Test
    public void addProjectToManager() throws Exception {

        managerRepository.save(manager);
        projectRepository.save(project);

        var updateProjectsDTO = new UpdateProjectsDTO(managerId.toString(), projectId.toString());

        mockMvc.perform(post("/api/v1/manager/projects/add")
                        .content(objectMapper.writeValueAsString(updateProjectsDTO))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/manager/{id}", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").isNotEmpty());

        mockMvc.perform(get("/api/v1/project/managers/{id}", projectId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void removeProjectFromManager() throws Exception {

        var savedManager = managerRepository.save(manager);
        var savedProject = projectRepository.save(project);

        savedManager.addProject(savedProject);
        managerRepository.save(savedManager);

        var updateProjectsDTO = new UpdateProjectsDTO(managerId.toString(), projectId.toString());

        mockMvc.perform(post("/api/v1/manager/projects/remove")
                        .content(objectMapper.writeValueAsString(updateProjectsDTO))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/manager/{id}", managerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").isEmpty());

        mockMvc.perform(get("/api/v1/project/managers/{id}", projectId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
