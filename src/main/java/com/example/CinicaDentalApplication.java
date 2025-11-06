package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// --- AÑADIR ESTAS IMPORTACIONES ---
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import com.example.model.entity.security.PermissionEntity;
import com.example.model.entity.security.RoleEntity;
import com.example.model.entity.security.RoleEnum;
import com.example.model.entity.security.UserEntity;
import com.example.repository.security.UserRepository;
import java.util.List;
import java.util.Set;
// --- FIN DE IMPORTACIONES ---


@SpringBootApplication
public class CinicaDentalApplication {

    public static void main(String[] args) {SpringApplication.run(CinicaDentalApplication.class, args);}

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            /* Create PERMISSIONS */
            PermissionEntity createPermission = PermissionEntity.builder()
                    .name("CREATE")
                    .build();
            PermissionEntity readPermission = PermissionEntity.builder()
                    .name("READ")
                    .build();
            PermissionEntity updatePermission = PermissionEntity.builder()
                    .name("UPDATE")
                    .build();
            PermissionEntity deletePermission = PermissionEntity.builder()
                    .name("DELETE")
                    .build();
            PermissionEntity refactorPermission = PermissionEntity.builder()
                    .name("REFACTOR")
                    .build();

            /* Create ROLES (con tu rol DOCTOR) */
            RoleEntity roleAdmin = RoleEntity.builder()
                    .roleEnum(RoleEnum.ADMIN)
                    .permissionList(Set.of(createPermission, readPermission, updatePermission, deletePermission))
                    .build();

            RoleEntity roleUser = RoleEntity.builder()
                    .roleEnum(RoleEnum.USER) // Rol para Pacientes
                    .permissionList(Set.of(createPermission, readPermission)) // Paciente (USER) puede crear y leer sus citas
                    .build();

            RoleEntity roleDoctor = RoleEntity.builder()
                    .roleEnum(RoleEnum.DOCTOR) // Tu rol DOCTOR
                    .permissionList(Set.of(createPermission, readPermission, updatePermission)) // Doctor puede Crear, Leer y Actualizar
                    .build();

            RoleEntity roleDeveloper = RoleEntity.builder()
                    .roleEnum(RoleEnum.DEVELOPER)
                    .permissionList(Set.of(createPermission, readPermission, updatePermission, deletePermission, refactorPermission))
                    .build();

            /* CREATE USERS (con contraseña "1234" para todos) */
            UserEntity userAdmin = UserEntity.builder()
                    .username("admin")
                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6") // pass: 1234
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleAdmin))
                    .build();

            UserEntity userPaciente = UserEntity.builder()
                    .username("paciente1")
                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6") // pass: 1234
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleUser))
                    .build();

            UserEntity userDoctor = UserEntity.builder()
                    .username("doctora")
                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6") // pass: 1234
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleDoctor))
                    .build();

            UserEntity userDev = UserEntity.builder()
                    .username("dev")
                    .password("$2a$10$cMY29RPYoIHMJSuwRfoD3eQxU1J5Rww4VnNOUOAEPqCBshkNfrEf6") // pass: 1234
                    .isEnabled(true)
                    .accountNoExpired(true)
                    .accountNoLocked(true)
                    .credentialNoExpired(true)
                    .roles(Set.of(roleDeveloper))
                    .build();

            userRepository.saveAll(List.of(userAdmin, userPaciente, userDoctor, userDev));
        };
    }
    // --- FIN DEL MÉTODO AÑADIDO ---
}