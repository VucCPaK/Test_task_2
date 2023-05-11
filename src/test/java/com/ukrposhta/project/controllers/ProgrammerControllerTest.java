package com.ukrposhta.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ukrposhta.project.dto.*;
import com.ukrposhta.project.entities.Programmer;
import com.ukrposhta.project.entities.Project;
import com.ukrposhta.project.enums.ProgrammerType;
import com.ukrposhta.project.enums.SkillLevel;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProgrammerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProgrammerRepository programmerRepository;

    @Autowired
    ObjectMapper objectMapper;

    private UUID programmerId;
    private Programmer programmer;
    private UUID projectId;
    private Project project;

    @BeforeEach
    public void initEach() {
        programmerId = UUID.randomUUID();
        programmer = Programmer.builder().id(programmerId).name("Antony").skillLevel(SkillLevel.MIDDLE.name())
                .type(ProgrammerType.DEVELOPER.name()).projects(new HashSet<>()).build();

        projectId = UUID.randomUUID();
        project = Project.builder().id(projectId).projectName("ukrposhta")
                .managers(new HashSet<>()).programmers(new HashSet<>()).build();
    }

    @Test
    public void mustCreateNewProgrammerAndReturnStringAsAnId() throws Exception {

        var createProgrammerDTO = new CreateProgrammerDTO("Antony", SkillLevel.MIDDLE, ProgrammerType.DEVELOPER);

        mockMvc.perform(post("/api/v1/programmer/new")
                        .content(objectMapper.writeValueAsString(createProgrammerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isString());
    }

    @Test
    public void mustReturnProgrammerWithCertainIdAndNameIsAntonyAndSkillLevelIsMiddleAndTypeIsDeveloper() throws Exception {

        programmerRepository.save(programmer);

        mockMvc.perform(get("/api/v1/programmer/{id}", programmerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(programmerId.toString()))
                .andExpect(jsonPath("$.name").value("Antony"))
                .andExpect(jsonPath("$.skillLevel").value("MIDDLE"))
                .andExpect(jsonPath("$.type").value("DEVELOPER"));
    }

    @Test
    public void mustReturnProgrammerNotFoundException() throws Exception {

        mockMvc.perform(get("/api/v1/programmer/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data doesn't match"));
    }

    @Test
    public void mustReturnAllProjectsThatOwnToProgrammer() throws Exception {

        var savedProgrammer = programmerRepository.save(programmer);
        var savedProject = projectRepository.save(project);

        savedProgrammer.addProject(savedProject);
        programmerRepository.save(savedProgrammer);

        mockMvc.perform(get("/api/v1/programmer/projects/{id}", programmerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void mustDeleteProgrammer() throws Exception {

        programmerRepository.save(programmer);

        mockMvc.perform(delete("/api/v1/programmer/{id}", programmerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void mustDeleteProgrammerAndDeleteProgrammerInAllProjects() throws Exception {

        var savedProgrammer = programmerRepository.save(programmer);
        var savedProject = projectRepository.save(project);

        savedProgrammer.addProject(savedProject);
        programmerRepository.save(savedProgrammer);

        mockMvc.perform(delete("/api/v1/programmer/{id}", programmerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/project/programmers/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty()); // array must be empty
    }

    @Test
    public void mustChangeNameAntonyToMichael() throws Exception {

        programmerRepository.save(programmer);

        var changeProgrammerNameDTO = new ChangeProgrammerNameDTO(programmerId.toString(), "Michael");

        mockMvc.perform(post("/api/v1/programmer/name")
                        .content(objectMapper.writeValueAsString(changeProgrammerNameDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/programmer/{id}", programmerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(programmerId.toString()))
                .andExpect(jsonPath("$.name").value("Michael"));
    }

    @Test
    public void mustChangeSkillLevelMiddleToSenior() throws Exception {

        programmerRepository.save(programmer);

        var changeProgrammerSkillLevelDTO = new ChangeProgrammerSkillLevelDTO(programmerId.toString(), SkillLevel.SENIOR);

        mockMvc.perform(post("/api/v1/programmer/skill")
                        .content(objectMapper.writeValueAsString(changeProgrammerSkillLevelDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/programmer/{id}", programmerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(programmerId.toString()))
                .andExpect(jsonPath("$.skillLevel").value("SENIOR"));
    }

    @Test
    public void mustChangeTypeDeveloperToQa() throws Exception {

        programmerRepository.save(programmer);

        var changeProgrammerTypeDTO = new ChangeProgrammerTypeDTO(programmerId.toString(), ProgrammerType.QA);

        mockMvc.perform(post("/api/v1/programmer/type")
                        .content(objectMapper.writeValueAsString(changeProgrammerTypeDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/programmer/{id}", programmerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(programmerId.toString()))
                .andExpect(jsonPath("$.skillLevel").value("QA"));
    }

    @Test
    public void addProjectToProgrammer() throws Exception {

        programmerRepository.save(programmer);
        projectRepository.save(project);

        var updateProjectsDTO = new UpdateProjectsDTO(programmerId.toString(), projectId.toString());

        mockMvc.perform(post("/api/v1/programmer/projects/add")
                        .content(objectMapper.writeValueAsString(updateProjectsDTO))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/programmer/{id}", programmerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").isNotEmpty());

        mockMvc.perform(get("/api/v1/project/programmers/{id}", projectId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    public void removeProjectFromManager() throws Exception {

        var savedProgrammer = programmerRepository.save(programmer);
        var savedProject = projectRepository.save(project);

        savedProgrammer.addProject(savedProject);
        programmerRepository.save(savedProgrammer);

        var updateProjectsDTO = new UpdateProjectsDTO(programmerId.toString(), projectId.toString());

        mockMvc.perform(post("/api/v1/programmer/projects/remove")
                        .content(objectMapper.writeValueAsString(updateProjectsDTO))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/programmer/{id}", programmerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projects").isEmpty());

        mockMvc.perform(get("/api/v1/project/programmers/{id}", projectId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
