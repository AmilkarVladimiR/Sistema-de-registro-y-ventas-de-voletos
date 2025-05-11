package com.vladimir.personas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vladimir.personas.model.PersonasModel;

public interface PersonasRepository extends JpaRepository<PersonasModel, Long> {

}
