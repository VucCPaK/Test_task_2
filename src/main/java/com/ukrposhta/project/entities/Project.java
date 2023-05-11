package com.ukrposhta.project.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Project")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Project {

    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String projectName;

    @JsonIgnore
    @ManyToMany(mappedBy = "projects", fetch = FetchType.EAGER)
    private Set<Manager> managers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "projects", fetch = FetchType.EAGER)
    private Set<Programmer> programmers = new HashSet<>();
}
