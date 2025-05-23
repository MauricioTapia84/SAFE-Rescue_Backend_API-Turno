package com.SAFE_Rescue.API_Turno.service;

import com.SAFE_Rescue.API_Turno.modelo.*;
import com.SAFE_Rescue.API_Turno.repository.CompaniaRepository;
import com.SAFE_Rescue.API_Turno.repository.EquipoRepository;
import com.SAFE_Rescue.API_Turno.repository.TipoEquipoRepository;
import com.SAFE_Rescue.API_Turno.repository.TurnoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class EquipoService {

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private TurnoService turnoService;

    @Autowired
    private CompaniaService companiaService;

    @Autowired
    private TipoEquipoService tipoEquipoService;

    @Autowired
    private CompaniaRepository companiaRepository;

    @Autowired
    private TipoEquipoRepository tipoEquipoRepository;

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private BomberoDTOService bomberoDTOService;

    public List<Equipo> findAll(){
        return equipoRepository.findAll();
    }

    public Equipo findByID(long id){
        return equipoRepository.findById(id).get();
    }

    public Equipo save(Equipo equipo) {
        try {
            Turno turno = equipo.getTurno();
            Compania compania = equipo.getCompania();
            TipoEquipo tipoEquipo = equipo.getTipoEquipo();

            validarEquipo(equipo);

            Turno guardadoTurno = turnoService.save(turno);
            Compania guardadaCompania = CompaniaService.save(compania);
            TipoEquipo guardadoTipoEquipo = TipoEquipoService.save(tipoEquipo);

            equipo.setTurno(guardadoTurno);
            equipo.setCompania(guardadaCompania);
            equipo.setTipoEquipo(guardadoTipoEquipo);

            return equipoRepository.save(equipo);
        } catch (EntityNotFoundException e) {
            throw new RuntimeException("Error al guardar el Equipo: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error inesperado: " + e.getMessage());
        }

    }

    public Equipo update(Equipo equipo, long id) {
        try {

            Equipo antiguoEquipo = equipoRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Equipo no encontrado"));

            if (equipo.getNombre() != null) {
                if (equipo.getNombre().length() > 50) {
                    throw new RuntimeException("El valor del nombre excede máximo de caracteres (50)");
                }
                antiguoEquipo.setNombre(equipo.getNombre());
            }

            if (equipo.getCantidadMiembros() != null) {
                if (String.valueOf(equipo.getCantidadMiembros()).length()> 2) {
                    throw new RuntimeException("El valor de la cantidad de miembros excede máximo de caracteres (2)");
                }
                antiguoEquipo.setCantidadMiembros(equipo.getCantidadMiembros());
            }

            if (equipo.getLider() != null) {
                if (equipo.getLider().length() > 50) {
                    throw new RuntimeException("El valor del lider excede máximo de caracteres (50)");
                }
                antiguoEquipo.setLider(equipo.getLider());
            }

            antiguoEquipo.setEstado(equipo.isEstado());

            return equipoRepository.save(antiguoEquipo);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el Equipo: " + e.getMessage());
        }
    }

    public void delete(long id){
        try {
            if (!equipoRepository.existsById(id)) {
                throw new NoSuchElementException("Equipo no encontrado");
            }

            equipoRepository.deleteById(id);

        } catch (Exception e) {
            throw new RuntimeException("Error al encontrar Equipo: " + e.getMessage());
        }

    }

    public void validarEquipo(@NotNull Equipo equipo) {

        if (equipo.getNombre().length() > 50) {
            throw new RuntimeException("El valor nombre excede máximo de caracteres (50)");
        }

        if (String.valueOf(equipo.getCantidadMiembros()).length()> 2) {
            throw new RuntimeException("El valor de la cantidad de miembros excede máximo de caracteres (2)");
        }

        if (equipo.getLider().length() > 50) {
            throw new RuntimeException("El valor del lider excede máximo de caracteres (50)");
        }

    }

    public void asignarCompania(long equipoId, long companiaId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        Compania compania = companiaRepository.findById(companiaId)
                .orElseThrow(() -> new RuntimeException("Compania no encontrada"));

        equipo.setCompania(compania);
        equipoRepository.save(equipo);
    }

    public void asignarTipoEquipo(long equipoId, long tipoEquipoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        TipoEquipo tipoEquipo = tipoEquipoRepository.findById(tipoEquipoId)
                .orElseThrow(() -> new RuntimeException("Tipo Equipo no encontrado"));

        equipo.setTipoEquipo(tipoEquipo);
        equipoRepository.save(equipo);
    }

    public void asignarTurno(long equipoId, long turnoId) {
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado"));

        Turno turno = turnoRepository.findById(turnoId)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        equipo.setTurno(turno);
        equipoRepository.save(equipo);
    }

    /**
     * Asigna una lista completa de bomberos a un equipo
     * @param equipoId ID del equipo a actualizar
     * @param bomberosIds Lista de IDs de bomberos a asignar
     * @throws RuntimeException Si el equipo no existe
     * @throws IllegalArgumentException Si la lista de bomberos es nula o vacía
     */
    public void asignarListaBomberos(long equipoId, List<Long> bomberosIds) {
        // Validación de parámetros
        if (bomberosIds == null || bomberosIds.isEmpty()) {
            throw new IllegalArgumentException("La lista de IDs de bomberos no puede ser nula o vacía");
        }

        // Buscar el equipo
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + equipoId));

        // Obtener los bomberos desde el servicio
        List<BomberoDTO> bomberos = new ArrayList<>();

        for (Long id : bomberosIds) {
            BomberoDTO bombero = bomberoDTOService.obtenerBomberoPorId(id);
            if (bombero != null) {
                bomberos.add(bombero);
            }
        }

        if (bomberos.isEmpty()) {
            throw new RuntimeException("No se encontraron bomberos válidos para asignar");
        }

        // Actualizar y guardar
        equipo.setPersonal(bomberos);
        equipoRepository.save(equipo);
    }

    /**
     * Asigna un solo bombero a un equipo
     * @param equipoId ID del equipo a actualizar
     * @param bomberoId ID del bombero a asignar
     * @throws RuntimeException Si el equipo o el bombero no existen
     */
    public void asignarBombero(long equipoId, long bomberoId) {
        // Buscar el equipo
        Equipo equipo = equipoRepository.findById(equipoId)
                .orElseThrow(() -> new RuntimeException("Equipo no encontrado con ID: " + equipoId));

        // Obtener el bombero
        BomberoDTO bombero = bomberoDTOService.obtenerBomberoPorId(bomberoId);
        if (bombero == null) {
            throw new RuntimeException("Bombero no encontrado con ID: " + bomberoId);
        }

        // Obtener la lista actual y agregar el nuevo bombero
        List<BomberoDTO> personal = equipo.getPersonal();
        if (personal == null) {
            personal = new ArrayList<>();
        }

        // Evitar duplicados
        if (!personal.contains(bombero)) {
            personal.add(bombero);
            equipo.setPersonal(personal);
            equipoRepository.save(equipo);
        }
    }

}
