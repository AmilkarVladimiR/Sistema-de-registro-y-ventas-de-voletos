package com.vladimir.personas.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vladimir.personas.repository.PersonasRepository;
import org.springframework.web.bind.annotation.GetMapping;
import com.vladimir.personas.model.PersonasModel;

@RestController
@RequestMapping("/personas")
public class PersonasController {
      
    @Autowired
    private PersonasRepository personasRepository;

    @GetMapping("/traer-personas")
    public List <PersonasModel> TraerPersonas() {
        return personasRepository.findAll();
        
    }
    

}
