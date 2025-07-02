package com.SAFE_Rescue.API_Turno;

import com.SAFE_Rescue.API_Turno.modelo.*;
import com.SAFE_Rescue.API_Turno.repository.*;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Clase encargada de cargar datos iniciales en la base de datos.
 * <p>
 * Esta clase se ejecuta solo en el perfil 'dev' y utiliza Faker para generar datos ficticios para las entidades
 * de la aplicación.
 * </p>
 */
@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired private BomberoRepository bomberoRepository;
    @Autowired private CompaniaRepository companiaRepository;
    @Autowired private EquipoRepository equipoRepository;
    @Autowired private RecursoRepository recursoRepository;
    @Autowired private TurnoRepository turnoRepository;
    @Autowired private TipoEquipoRepository tipoEquipoRepository;
    @Autowired private UbicacionRepository ubicacionRepository;
    @Autowired private VehiculoRepository vehiculoRepository;

    /**
     * Método que se ejecuta al iniciar la aplicación.
     * <p>
     * Genera datos ficticios para las entidades TipoEquipo, Ubicacion, Compania, Turno y Equipo.
     * </p>
     *
     * @param args Argumentos de línea de comandos
     * @throws Exception si ocurre un error durante la ejecución
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataLoader is running...");

        Faker faker = new Faker();
        Random random = new Random();
        Set<String> uniqueNombres = new HashSet<>();

        // Generar ubicaciones
        for (int i = 0; i < 5; i++) {
            Ubicacion ubicacion = new Ubicacion();
            ubicacion.setNumeracion(faker.number().numberBetween(1,9999));
            ubicacion.setCalle(faker.country().name());
            ubicacion.setComuna(faker.country().name());
            ubicacion.setRegion(faker.country().capital());
            try {
                ubicacionRepository.save(ubicacion);
            } catch (Exception e) {
                System.out.println("Error al guardar ubicacion: " + e.getMessage());
            }
        }

        List<Ubicacion> ubicaciones = ubicacionRepository.findAll();
        if (ubicaciones.isEmpty()) {
            System.out.println("No se encontraron ubicaciones, agregue ubicaciones primero");
            return;
        }

        // Generar TipoEquipo
        for (int i = 0; i < 3; i++) {
            TipoEquipo tipoEquipo = new TipoEquipo();
            tipoEquipo.setNombre(faker.animal().name());
            try {
                tipoEquipoRepository.save(tipoEquipo);
            } catch (Exception e) {
                System.out.println("Error al guardar tipo equipo: " + e.getMessage());
            }
        }

        List<TipoEquipo> tiposEquipos = tipoEquipoRepository.findAll();
        if (tiposEquipos.isEmpty()) {
            System.out.println("No se encontraron tipo equipo, agregue tipos equipos primero");
            return;
        }


        // Generar Compania
        for (int i = 0; i < 5; i++) {
            Compania compania = new Compania();
            String nombre;
            do {
                nombre = faker.company().name();
            } while (uniqueNombres.contains(nombre));
            uniqueNombres.add(nombre);
            compania.setNombre(nombre);
            Ubicacion ubicacion = ubicaciones.get(random.nextInt(ubicaciones.size()));
            compania.setUbicacion(ubicacion);
            try {
                companiaRepository.save(compania);
            } catch (Exception e) {
                System.out.println("Error al guardar compania: " + e.getMessage());
            }
        }

        List<Compania> companias = companiaRepository.findAll();
        if (companias.isEmpty()) {
            System.out.println("No se encontraron companias, agregue companias primero");
            return;
        }

        // Generar Turnos
        List<String> Detallesturnos = Arrays.asList("mañana", "tarde", "noche");
        for (int i = 0; i < 3; i++) {
            Turno turno = new Turno();
            String nombreTurno = Detallesturnos.get(random.nextInt(Detallesturnos.size()));
            turno.setNombre(nombreTurno);

            // Generar fechaHoraInicio aleatoria en las próximas 10 días
            LocalDateTime fechaHoraInicio = LocalDateTime.now().plusDays(random.nextInt(10))
                    .withHour(random.nextInt(24))
                    .withMinute(random.nextInt(60));

            LocalDateTime fechaHoraFin = fechaHoraInicio.plusHours(8);
            turno.setFechaHoraInicio(fechaHoraInicio);
            turno.setFechaHoraFin(fechaHoraFin);
            turno.setDuracion((int) java.time.Duration.between(turno.getFechaHoraInicio(), turno.getFechaHoraFin()).toHours());

            try {
                turnoRepository.save(turno);
            } catch (Exception e) {
                System.out.println("Error al guardar turno: " + e.getMessage());
            }
        }

        // Generar Equipo
        List<Turno> turnos = turnoRepository.findAll();
        if (turnos.isEmpty()) {
            System.out.println("No se encontraron turnos, agregue turnos primero");
            return;
        }

        List<Bombero> bomberos = bomberoRepository.findAll();
        if (bomberos.isEmpty()) {
            System.out.println("No se encontraron bomberos, agregue bomberos primero");
            return;
        }

        List<Vehiculo> vehiculos = vehiculoRepository.findAll();
        if (vehiculos.isEmpty()) {
            System.out.println("No se encontraron vehiculos, agregue vehiculos primero");
            return;
        }

        List<Recurso> recursos = recursoRepository.findAll();
        if (recursos.isEmpty()) {
            System.out.println("No se encontraron recursos, agregue recursos primero");
            return;
        }

        for (int i = 0; i < 5; i++) {
            Equipo equipo = new Equipo();
            List<Vehiculo> vehiculosAsignados = new ArrayList<>();
            List<Bombero> personal = new ArrayList<>();
            List<Recurso> recursosAsignados = new ArrayList<>();
            equipo.setNombre(faker.name().firstName());
            equipo.setCantidadMiembros(faker.number().numberBetween(0, 99));
            equipo.setEstado(faker.random().nextBoolean());
            equipo.setLider(faker.name().firstName());

            for (int j = 0; j < 3; j++) {
                vehiculosAsignados.add(vehiculos.get(random.nextInt(vehiculos.size())));
            }
            equipo.setVehiculos(vehiculosAsignados);

            for (int j = 0; j < 3; j++) {
                personal.add(bomberos.get(random.nextInt(bomberos.size())));
            }
            equipo.setPersonal(personal);

            for (int j = 0; j < 3; j++) {
                recursosAsignados.add(recursos.get(random.nextInt(recursos.size())));
            }
            equipo.setRecursos(recursosAsignados);

            Turno turno = turnos.get(random.nextInt(turnos.size()));
            equipo.setTurno(turno);
            Compania compania = companias.get(random.nextInt(companias.size()));
            equipo.setCompania(compania);
            TipoEquipo tipoEquipo = tiposEquipos.get(random.nextInt(tiposEquipos.size()));
            equipo.setTipoEquipo(tipoEquipo);

            try {
                equipoRepository.save(equipo);
            } catch (Exception e) {
                System.out.println("Error al guardar equipo: " + e.getMessage());
            }
        }
    }
}