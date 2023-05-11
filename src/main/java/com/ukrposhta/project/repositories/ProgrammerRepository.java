package com.ukrposhta.project.repositories;

import com.ukrposhta.project.entities.Programmer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProgrammerRepository extends JpaRepository<Programmer, UUID> {
}
