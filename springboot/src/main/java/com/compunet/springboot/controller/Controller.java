package com.compunet.springboot.controller;

import com.compunet.springboot.repository.EstudianteRepository;
import com.compunet.springboot.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.compunet.springboot.model.Curso;
import com.compunet.springboot.model.Estudiante;
import com.compunet.springboot.model.Permiso;
import com.compunet.springboot.model.Profesor;
import com.compunet.springboot.model.Usuario;
import com.compunet.springboot.repository.CursoRepository;
import com.compunet.springboot.repository.ProfesorRepository;
import com.compunet.springboot.service.CursoService;
import com.compunet.springboot.service.EstudianteService;
import com.compunet.springboot.service.MatriculaService;
import com.compunet.springboot.service.PermisoService;
import com.compunet.springboot.service.ProfesorService;
import com.compunet.springboot.service.UsuarioService;

@RestController
public class Controller {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final ProfesorRepository profesorRepository;
    private final UsuarioService usuarioService;
    private final ProfesorService profesorService;
    private final CursoService cursoService;
    private final EstudianteService estudianteService;
    private final MatriculaService matriculaService;
    private final PermisoService permisoService;

    public Controller(CursoRepository cursoRepo,
            ProfesorRepository profesorRepo,
            UsuarioService usuarioService,
            ProfesorService profesorService,
            CursoService cursoService,
            EstudianteService estudianteService,
            MatriculaService matriculaService,
            PermisoService permisoService, EstudianteRepository estudianteRepository, UsuarioRepository usuarioRepository) {
        this.cursoRepository = cursoRepo;
        this.profesorRepository = profesorRepo;
        this.usuarioService = usuarioService;
        this.profesorService = profesorService;
        this.cursoService = cursoService;
        this.estudianteService = estudianteService;
        this.matriculaService = matriculaService;
        this.permisoService = permisoService;
        this.estudianteRepository = estudianteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/")
    public String home() {
        return "!Proyecto Spring boot funcionando correctamente¡";
    }

    @GetMapping("/cursos")
    public List<Curso> getCursos() {
        return cursoRepository.findAll();
    }

    @GetMapping("/profesores")
    public List<Profesor> getProfesors() {
        return profesorRepository.findAll();
    }

    @GetMapping("/usuario")
    public List<Usuario> getUsuario() {
        return usuarioService.listarUsuariosActivos();
    }

    @GetMapping("/crear-usuario")
    public Usuario crearUsuario() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre("Alejandro");
        nuevoUsuario.setApellido("Peñaranda");
        nuevoUsuario.setCorreoInstitucional("apenaranda@icesi.edu.co");
        nuevoUsuario.setPassword("password");

        return usuarioService.registrarUsuario(nuevoUsuario, "ESTUDIANTE");
    }

    // ==========================================
    // EJERCICIOS DE QUERY METHODS Y CONSULTAS
    // ==========================================

    // Ejercicio 1: Buscar usuario por correo exacto
    @GetMapping("/ejercicio1")
    public Optional<Usuario> ejercicio1BuscarUsuarioPorCorreo() {
        return usuarioService.obtenerPorCorreo("apenaranda@icesi.edu.co");
    }

    // Ejercicio 2: Verificar existencia de correo
    @GetMapping("/ejercicio2")
    public boolean ejercicio2ExisteCorreo() {
        return usuarioService.existeCorreo("apenaranda@icesi.edu.co");
    }

    // Ejercicio 3: Profesores activos por departamento
    @GetMapping("/ejercicio3")
    public List<Profesor> ejercicio3ProfesoresActivosPorDepartamento() {
        return profesorService.listarProfesoresActivosPorDepartamento("Computación y Sistemas Inteligentes");
    }

    // Ejercicio 4: Cursos por rango de créditos
    @GetMapping("/ejercicio4")
    public List<Curso> ejercicio4CursosPorRangoCreditos() {
        return cursoService.listarPorRangoCreditos(2, 4);
    }

    // Ejercicio 5: Buscar cursos por coincidencia en el nombre
    @GetMapping("/ejercicio5")
    public List<Curso> ejercicio5BuscarCursosPorNombre() {
        return cursoService.buscarCursosPorNombre("Sistemas");
    }

    // Ejercicio 6: Profesores por especialidad ordenados por apellido
    @GetMapping("/ejercicio6")
    public List<Profesor> ejercicio6ProfesoresPorEspecialidadOrdenados() {
        return profesorService.listarPorEspecialidadOrdenados("Software");
    }

    // Ejercicio 7: Estudiantes por dominio de correo
    @GetMapping("/ejercicio7")
    public List<Estudiante> ejercicio7EstudiantesPorDominio() {
        return estudianteService.filtrarPorDominio("@icesi.edu.co");
    }

    // Ejercicio 8: Conteo de estudiantes activos
    @GetMapping("/ejercicio8")
    public long ejercicio8ConteoEstudiantesActivos() {
        return estudianteService.contarEstudiantesActivos();
    }

    // Ejercicio 9: Cursos asignados a un profesor (ManyToOne)
    @GetMapping("/ejercicio9")
    public List<Curso> ejercicio9CursosDeProfesor() {
        return cursoService.listarCursosDeProfesor(1L);
    }

    // Ejercicio 10: Cursos según el departamento del profesor
    @GetMapping("/ejercicio10")
    public List<Curso> ejercicio10CursosPorDepartamentoDelProfesor() {
        return cursoService.listarCursosPorDepartamentoDelProfesor("Computación y Sistemas Inteligentes");
    }

    // Ejercicio 11: Usuarios activos por nombre de rol (ManyToMany)
    @GetMapping("/ejercicio11")
    public List<Usuario> ejercicio11UsuariosActivosPorRol() {
        return usuarioService.listarUsuariosActivosPorRol("ESTUDIANTE");
    }

    // Ejercicio 12: Verificar matrícula en tabla intermedia
    @GetMapping("/ejercicio12")
    public boolean ejercicio12VerificarMatricula() {
        return matriculaService.estaMatriculado(1L, 1L);
    }

    // Ejercicio 13: Cursos por créditos mínimos ordenados descendentemente
    @GetMapping("/ejercicio13")
    public List<Curso> ejercicio13CursosPorCreditosMinimos() {
        return cursoService.obtenerCursosPorCreditosMinimos(3);
    }

    // Ejercicio 14: Permisos de un rol (ManyToMany inversa)
    @GetMapping("/ejercicio14")
    public List<Permiso> ejercicio14PermisosDeRol() {
        return permisoService.listarPermisosDeRol("ADMIN");
    }

    // Ejercicio 15: Estudiantes de un curso con @Query (JPQL)
    @GetMapping("/ejercicio15-jpql")
    public List<Estudiante> ejercicio15EstudiantesCursoJPQL() {
        return estudianteService.listarEstudiantesDeCursoJPQL(1L);
    }

    // Ejercicio 15: Estudiantes de un curso con @Query (Native SQL)
    @GetMapping("/ejercicio15-nativo")
    public List<Estudiante> ejercicio15EstudiantesCursoNativo() {
        return estudianteService.listarEstudiantesDeCursoNativo(1L);
    }


    // Ejercicio dificil 1 
    @GetMapping("/ejercicio/dificil1")
    public List<Profesor> dificil1() {
        return profesorService.ejercicioDificil1("Computación y Sistemas Inteligentes", 3);
    }

    // Ejercicio dificil 1 
    @GetMapping("/ejercicio/dificil2")
    public List<Estudiante> dificil2() {
        return estudianteRepository.findDistinctByActiveTrueAndCorreoInstitucionalEndingWithAndEstudianteCursos_Curso_Profesor_EspecialidadIgnoreCaseOrderByApellidoAscNombreAsc("icesi.edu.co", "telematica");
    }

    // Ejercicio dificil 3 
    @GetMapping("/ejercicio/dificil3")
    public List<Profesor> dificil3() {
        return profesorService.ejercicioDificil1("Computación y Sistemas Inteligentes", 3);
    }

    // Ejercicio dificil 4 
    @GetMapping("/ejercicio/dificil4")
    public List<Curso> dificil4() {
        return cursoRepository.findTop5ByDepartamentoInAndProfesor_ApellidoIgnoreCaseAndEstudianteCursos_Estudiante_IdInOrderByCreditosDesc(List.of("Computación y Sistemas Inteligentes"), "Rincon", List.of(1L, 2L, 3L));
    }

    // Ejercicio dificil 5
    @GetMapping("/ejercicio/dificil5")
    public List<Usuario> dificil5() {
        return usuarioRepository.findDistinctByActiveTrueAndRoles_NombreInAndRoles_Permisos_NombreIgnoreCaseOrderByApellidoAscNombreAsc(List.of("ADMIN", "ESTUDIANTE"), "COURSE_READ");
    }


}
