package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.Equipo;
import com.SAFE_Rescue.API_Turno.repository.TurnoRepository;
import com.SAFE_Rescue.API_Turno.modelo.Turno;
import com.SAFE_Rescue.API_Turno.repository.EquipoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class TurnoService {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private EquipoRepository equipoRepository;

    public List<Turno> findAll(){
        return turnoRepository.findAll();
    }

    public Turno findByID(long id){
        return turnoRepository.findById(id).get();
    }

    public Turno save(Turno turno) {
        try {
            Equipo equipo = turno.getEquipo();

            validarCiudadano(turno);

            Equipo guardadaEquipo = equipoService.save(equipo);

            turno.setEquipo(guardadaEquipo);

            return turnoRepository.save(turno);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Error: el correo de la credencial ya está en uso.");
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Error al guardar el Ciudadano: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado: " + e.getMessage());
        }
    }

    public Turno update(Turno turno, long id) {
        try {

            Turno antiguoTurno = turnoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Ciudadano no encontrado"));

            //Control de errores
            if (turno.getNombre() != null) {
                if (turno.getNombre().length() > 50) {
                    throw new RuntimeException("El valor nombre excede máximo de caracteres (50)");
                }
                antiguoTurno.setNombre(turno.getNombre());
            }

            if (turno.getTelefono() != null) {
                if (turnoRepository.existsByTelefono(turno.getTelefono())) {
                    throw new RuntimeException("El Telefono ya existe");
                }else{
                    if (String.valueOf(turno.getTelefono()).length()> 9) {
                        throw new RuntimeException("El valor telefono excede máximo de caracteres (9)");
                    }
                    antiguoTurno.setTelefono(turno.getTelefono());
                }
            }

            if (turno.getRun() != null) {
                if (turnoRepository.existsByRun(turno.getRun())) {
                    throw new RuntimeException("El RUN ya existe");
                }else{
                    if (String.valueOf(turno.getRun()).length() > 8) {
                        throw new RuntimeException("El valor RUN excede máximo de caracteres (8)");
                    }
                    antiguoTurno.setRun(turno.getRun());
                }
            }

            if (turno.getDv() != null) {
                if (turno.getDv().length() > 1) {
                    throw new RuntimeException("El valor DV excede máximo de caracteres (1)");
                }
                antiguoTurno.setDv(turno.getDv());
            }

            if (turno.getA_paterno() != null) {
                if (turno.getA_paterno().length() > 50) {
                    throw new RuntimeException("El valor a_paterno excede máximo de caracteres (50)");
                }
                antiguoTurno.setA_paterno(turno.getA_paterno());
            }

            if (turno.getA_materno() != null) {
                if (turno.getA_materno().length() > 50) {
                    throw new RuntimeException("El valor a_materno excede máximo de caracteres (50)");
                }
                antiguoTurno.setA_materno(turno.getA_materno());
            }

            if (turno.getFecha_registro() != null) {
                antiguoTurno.setFecha_registro(turno.getFecha_registro());
            }


            return turnoRepository.save(antiguoTurno);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el Ciudadano: " + e.getMessage());
        }
    }

    public void delete(long id){
        try {
            if (!turnoRepository.existsById(id)) {
                throw new NoSuchElementException("Ciudadano no encontrado");
            }
            turnoRepository.deleteById(id);

        } catch (Exception e) {
            throw new RuntimeException("Error al encontrar Ciudadano: " + e.getMessage());
        }
    }

    public void validarCiudadano(@NotNull Turno turno) {

        if (turnoRepository.existsByRun(turno.getRun())) {
            throw new RuntimeException("El RUN ya existe");
        }

        if (turnoRepository.existsByTelefono(turno.getTelefono())) {
            throw new RuntimeException("El Telefono ya existe");
        }

        if (String.valueOf(turno.getRun()).length() > 8) {
            throw new RuntimeException("El valor RUN excede máximo de caracteres (8)");
        }

        if (turno.getDv().length() > 1) {
            throw new RuntimeException("El valor DV excede máximo de caracteres (1)");
        }

        if (turno.getNombre().length() > 50) {
            throw new RuntimeException("El valor nombre excede máximo de caracteres (50)");
        }

        if (turno.getA_paterno().length() > 50) {
            throw new RuntimeException("El valor a_paterno excede máximo de caracteres (50)");
        }

        if (turno.getA_materno().length() > 50) {
            throw new RuntimeException("El valor a_materno excede máximo de caracteres (50)");
        }

        if (String.valueOf(turno.getTelefono()).length()> 9) {
            throw new RuntimeException("El valor telefono excede máximo de caracteres (9)");
        }

    }

    public void asignarCredencial(long ciudadanoId, long credencialId) {
        Turno turno = turnoRepository.findById(ciudadanoId)
                .orElseThrow(() -> new RuntimeException("Ciudadano no encontrado"));

        Equipo equipo = equipoRepository.findById(credencialId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrada"));

        turno.setEquipo(equipo);
        turnoRepository.save(turno);
    }

}