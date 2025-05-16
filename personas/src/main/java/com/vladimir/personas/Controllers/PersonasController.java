package com.vladimir.personas.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.vladimir.personas.repository.PersonasRepository;
import com.vladimir.personas.model.PersonasModel;

@RestController
@RequestMapping("/personas")
@CrossOrigin(origins = "*")
public class PersonasController {

    @Autowired
    private PersonasRepository personasRepository;

    @GetMapping("/traer-personas")
    public List<PersonasModel> TraerPersonas() {
        return personasRepository.findAll();
    }

    @GetMapping("/traer-persona/{id}")
    public ResponseEntity<PersonasModel> TraerPersona(@PathVariable Long id) {
        return personasRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🚫 No insertar si el asiento ya está ocupado
    @PostMapping("/insertar-personas")
    public ResponseEntity<?> insertarPersonas(@RequestBody PersonasModel persona) {
        boolean asientoOcupado = personasRepository.findAll().stream()
              .anyMatch(p -> p.getNumeroAsiento() == persona.getNumeroAsiento());
 

        if (asientoOcupado) {
            return ResponseEntity.badRequest().body("El número de asiento ya está ocupado.");
        }

        PersonasModel personaGuardada = personasRepository.save(persona);
        return ResponseEntity.ok(personaGuardada);
    }

    @PutMapping("/editar-personas/{id}")
    public ResponseEntity<PersonasModel> actualizarPersonas(@PathVariable Long id, @RequestBody PersonasModel persona) {
        return personasRepository.findById(id).map(existingPersona -> {
            existingPersona.setNumero(persona.getNumero());
            existingPersona.setNombre(persona.getNombre());
            existingPersona.setApellido(persona.getApellido());
            existingPersona.setOrigen(persona.getOrigen());
            existingPersona.setDestino(persona.getDestino());
            existingPersona.setPrecio(persona.getPrecio());
            existingPersona.setNumeroAsiento(persona.getNumeroAsiento());
            PersonasModel updatedPersona = personasRepository.save(existingPersona);
            return ResponseEntity.ok(updatedPersona);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/eliminar-personas/{id}")
    public void eliminarpersonas(@PathVariable Long id) {
        personasRepository.deleteById(id);
    }
}
