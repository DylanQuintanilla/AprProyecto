package com.example;

import com.example.model.entity.Dentista;
import com.example.model.entity.Paciente;
import com.example.model.entity.security.PermissionEntity;
import com.example.model.entity.security.RoleEntity;
import com.example.model.entity.security.RoleEnum;
import com.example.model.entity.security.UserEntity;
import com.example.repository.DentistaRepository;
import com.example.repository.PacienteRepository;
// --- CAMBIOS AQUÍ: Importar nuevos repositorios ---
import com.example.repository.security.PermissionRepository;
import com.example.repository.security.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class CinicaDentalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CinicaDentalApplication.class, args);
    }

    // --- CAMBIO AQUÍ: Inyectar nuevos repositorios ---
    @Bean
    CommandLineRunner init(PacienteRepository pacienteRepository,
                           DentistaRepository dentistaRepository,
                           PermissionRepository permissionRepository,
                           RoleRepository roleRepository) {
        return args -> {

            // --- 1. CREAR Y GUARDAR PERMISOS PRIMERO ---
            PermissionEntity citasLeerPropias = PermissionEntity.builder().name("citas:leer:propias").build();
            PermissionEntity citasSolicitarPropias = PermissionEntity.builder().name("citas:solicitar:propias").build();
            PermissionEntity perfilLeerPropio = PermissionEntity.builder().name("perfil:leer:propio").build();
            PermissionEntity perfilActualizarPropio = PermissionEntity.builder().name("perfil:actualizar:propio").build();
            PermissionEntity antecedentesLeerPropios = PermissionEntity.builder().name("antecedentes:leer:propios").build();

            PermissionEntity citasLeerAsignadas = PermissionEntity.builder().name("citas:leer:asignadas").build();
            PermissionEntity citasGestionarAsignadas = PermissionEntity.builder().name("citas:gestionar:asignadas").build();
            PermissionEntity pacientesLeerLista = PermissionEntity.builder().name("pacientes:leer:lista").build();
            PermissionEntity pacientesLeerPerfil = PermissionEntity.builder().name("pacientes:leer:perfil").build();
            PermissionEntity antecedentesLeerPaciente = PermissionEntity.builder().name("antecedentes:leer:paciente").build();
            PermissionEntity antecedentesGestionarPaciente = PermissionEntity.builder().name("antecedentes:gestionar:paciente").build();

            PermissionEntity citasAdmin = PermissionEntity.builder().name("citas:admin").build();
            PermissionEntity pacientesAdmin = PermissionEntity.builder().name("pacientes:admin").build();
            PermissionEntity dentistasAdmin = PermissionEntity.builder().name("dentistas:admin").build();
            PermissionEntity antecedentesAdmin = PermissionEntity.builder().name("antecedentes:admin").build();
            PermissionEntity clinicaAdmin = PermissionEntity.builder().name("clinica:admin").build();

            // Guardar todos los permisos en la base de datos
            permissionRepository.saveAll(List.of(
                    citasLeerPropias, citasSolicitarPropias, perfilLeerPropio, perfilActualizarPropio, antecedentesLeerPropios,
                    citasLeerAsignadas, citasGestionarAsignadas, pacientesLeerLista, pacientesLeerPerfil, antecedentesLeerPaciente, antecedentesGestionarPaciente,
                    citasAdmin, pacientesAdmin, dentistasAdmin, antecedentesAdmin, clinicaAdmin
            ));


            // --- 2. CREAR Y GUARDAR ROLES SEGUNDO ---
            RoleEntity roleUser = RoleEntity.builder()
                    .roleEnum(RoleEnum.USER)
                    .permissionList(Set.of(
                            citasLeerPropias,
                            citasSolicitarPropias,
                            perfilLeerPropio,
                            perfilActualizarPropio,
                            antecedentesLeerPropios
                    ))
                    .build();

            RoleEntity roleDoctor = RoleEntity.builder()
                    .roleEnum(RoleEnum.DOCTOR)
                    .permissionList(Set.of(
                            citasLeerAsignadas,
                            citasGestionarAsignadas,
                            perfilLeerPropio,
                            perfilActualizarPropio,
                            pacientesLeerLista,
                            pacientesLeerPerfil,
                            antecedentesLeerPaciente,
                            antecedentesGestionarPaciente
                    ))
                    .build();

            RoleEntity roleAdmin = RoleEntity.builder()
                    .roleEnum(RoleEnum.ADMIN)
                    .permissionList(Set.of(
                            citasAdmin,
                            pacientesAdmin,
                            dentistasAdmin,
                            antecedentesAdmin,
                            clinicaAdmin
                    ))
                    .build();

            // Guardar todos los roles en la base de datos
            roleRepository.saveAll(List.of(roleUser, roleDoctor, roleAdmin));


            // --- 3. CREAR USUARIOS (SIN GUARDARLOS AÚN) ---
            UserEntity userPaciente = UserEntity.builder()
                    .username("paciente1")
                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6") // pass: 1234
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleUser)) // Asignamos el rol que YA existe en la DB
                    .build();

            UserEntity userDoctor = UserEntity.builder()
                    .username("doctora")
                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6") // pass: 12234
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleDoctor)) // Asignamos el rol que YA existe en la DB
                    .build();

            UserEntity userAdmin = UserEntity.builder()
                    .username("admin")
                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6") // pass: 1234
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleAdmin)) // Asignamos el rol que YA existe en la DB
                    .build();

            // --- 4. CREAR PACIENTES/DENTISTAS Y GUARDARLOS (ESTO GUARDARÁ LOS USUARIOS EN CASCADA) ---
            Paciente paciente = Paciente.builder()
                    .nombre("Carlos")
                    .apellido("Lopez")
                    .email("paciente1@mail.com")
                    .fechaNacimiento(new Date())
                    .dui("12345678-1")
                    .user(userPaciente)
                    .build();

            Dentista dentista = Dentista.builder()
                    .nombre("Ana")
                    .apellido("Martinez")
                    .email("doctora@mail.com")
                    .user(userDoctor)
                    .build();

            Paciente adminComoPaciente = Paciente.builder()
                    .nombre("Admin")
                    .apellido("Root")
                    .email("admin@mail.com")
                    .fechaNacimiento(new Date())
                    .dui("00000000-0")
                    .user(userAdmin)
                    .build();

            pacienteRepository.saveAll(List.of(paciente, adminComoPaciente));
            dentistaRepository.save(dentista);
        };
    }
}