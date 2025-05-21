package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.Equipo;
import com.SAFE_Rescue.API_Turno.repository.EquipoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    public List<Equipo> findAll(){
        return equipoRepository.findAll();
    }

    public Equipo findByID(long id){
        return equipoRepository.findById(id).get();
    }

    public Equipo save(Equipo equipo) {
        try {
            return equipoRepository.save(equipo);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Error al guardar la equipo: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado: " + e.getMessage());
        }
    }

    public Equipo update(Equipo equipo, long id) {
        try {

            Equipo antiguoEquipo = equipoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Equipo no encontrada"));

            if (equipo.getContrasenia() != null) {
                if (equipo.getContrasenia().length() > 16) {
                    throw new RuntimeException("El valor contrasenia excede máximo de caracteres (16)");
                }
                antiguaEquipo.setContrasenia(equipo.getContrasenia());
            }

            if (equipo.getCorreo() != null) {
                if (equipoRepository.existsByCorreo(equipo.getCorreo())) {
                    throw new RuntimeException("El Correo ya existe");
                }else{
                    if (equipo.getCorreo().length() > 80) {
                        throw new RuntimeException("El valor correo excede máximo de caracteres (80)");
                    }
                    antiguaEquipo.setCorreo(equipo.getCorreo());
                }
            }

            antiguaEquipo.setActivo(equipo.isActivo());

            return equipoRepository.save(antiguaEquipo);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el Ciudadano: " + e.getMessage());
        }
    }

    public void delete(long id){
        try {
            if (!equipoRepository.existsById(id)) {
                throw new NoSuchElementException("Equipo no encontrada");
            }

            Equipo equipoVacia = equipoRepository.findById(id).get();

            equipoRepository.save(equipoVacia);

        } catch (Exception e) {
            throw new RuntimeException("Error al encontrar Equipo: " + e.getMessage());
        }
    }



}
