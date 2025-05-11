package com.vladimir.personas.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vladimir.personas.repository.PersonasRepository;
import org.springframework.web.bind.annotation.GetMapping;
import com.vladimir.personas.model.PersonasModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/personas")
public class PersonasController {
      //Metodo get para traer a todas las personas de la base de datos
    @Autowired
    private PersonasRepository personasRepository;

    @GetMapping("/traer-personas")
    public List <PersonasModel> TraerPersonas() {
        return personasRepository.findAll();
    }
    //Metodo para insertar una persona en la base de datos 
    @PostMapping("/insertar-personas")
    public PersonasModel insertarPersona(@RequestBody PersonasModel persona) {
        return personasRepository.save(persona);
       
    }

}
