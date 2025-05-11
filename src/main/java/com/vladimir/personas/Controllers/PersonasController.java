package com.vladimir.personas.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vladimir.personas.repository.PersonasRepository;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import com.vladimir.personas.model.PersonasModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;



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
    public PersonasModel insertarPersonas(@RequestBody PersonasModel persona) {
        return personasRepository.save(persona);
       
    }
    //Metodo para  editar una persona en la base de datos
    @PutMapping("/editar-personas/{id}")
   public ResponseEntity<PersonasModel> actualizarPersonas(@PathVariable Long id, @RequestBody PersonasModel persona) {
    return personasRepository.findById(id).map(existingPersona -> {
                existingPersona.setNombre(persona.getNombre());
                existingPersona.setApellido(persona.getApellido());
                existingPersona.setEmail(persona.getEmail());
                existingPersona.setNumeroControl(persona.getNumeroControl());
                existingPersona.setTelefono(persona.getTelefono());
                existingPersona.setCarrera(persona.getCarrera());
               existingPersona.setImagenURL(persona.getImagenURL());
                PersonasModel updatedPersona = personasRepository.save(existingPersona);
                return ResponseEntity.ok(updatedPersona);
            })
            .orElse(ResponseEntity.notFound().build());
       
    }
    //Metodo para eliminar una persona en la base de datos
    @DeleteMapping("/eliminar-personas/{id}")
    public void eliminarpersonas(@PathVariable Long id) {
        personasRepository.deleteById(id);
    }
  }

