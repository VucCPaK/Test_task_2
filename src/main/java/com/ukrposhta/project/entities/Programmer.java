package com.ukrposhta.project.entities;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Programmer")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Programmer {

    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "skill_level", nullable = false)
    private String skillLevel;

    @Column(name = "type", nullable = false)
    private String type;

    @ManyToMany(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinTable(
            name = "Programmer_Project",
            joinColumns = {@JoinColumn(name = "programmer_id")},
            inverseJoinColumns = {@JoinColumn(name = "project_id")}
    )
    private Set<Project> projects = new HashSet<>();

    public void addProject(Project project) {
        projects.add(project);
    }

    public void removeProject(Project project) {
        projects.remove(project);
    }
}
