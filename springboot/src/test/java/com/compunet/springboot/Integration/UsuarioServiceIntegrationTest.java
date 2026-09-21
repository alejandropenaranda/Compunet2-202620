package com.compunet.springboot.Integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.compunet.springboot.model.Rol;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.repository.RolRepository;
import com.compunet.springboot.service.UsuarioService;

@SpringBootTest 
@ActiveProfiles ("test")
@DisplayName ("Pruebas de Integración - UsuarioService con base de datos H2 en memoria")
public class UsuarioServiceIntegrationTest {
    
    @Autowired 
    private UsuarioService usuarioService;
    
    @Autowired 
    private RolRepository rolRepository;

    @Test 
    @DisplayName ("Debe persistir el usuario con su rol en la DB H2 y poder consultarlo")
    void debeRegistrarYConsultarUsuarioEnBaseDeDatosReal() {
        Rol rolAdmin = new Rol();
        rolAdmin.setNombre("ADMINISTRADOR_TEST");
        rolRepository.save(rolAdmin);

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Laura");
        nuevoUsuario.setApellido("Gomez");
        nuevoUsuario.setCorreoInstitucional("lgomez@icesi.edu.co");
        nuevoUsuario.setPassword("Segura123");

        Usuario usuarioGuardado = usuarioService.registrarUsuario(nuevoUsuario, "ADMINISTRADOR_TEST");

        assertNotNull(usuarioGuardado.getId(), "la base de datos debe generar el ID autoincremental");
        assertTrue(usuarioGuardado.isActive());
        assertEquals("lgomez@icesi.edu.co", usuarioGuardado.getCorreoInstitucional());

        List<Usuario> usuariosAdmin = usuarioService.listarUsuariosActivosPorRol("ADMINISTRADOR_TEST");
        assertFalse(usuariosAdmin.isEmpty(), "Debe encontrar al menos un usuario con el rol");
        assertTrue(usuariosAdmin.stream().anyMatch(u -> u.getCorreoInstitucional().equals("lgomez@icesi.edu.co")));

    }

}
