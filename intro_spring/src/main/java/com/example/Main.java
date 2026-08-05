package com.example;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.example.model.CarritoMatricula;
import com.example.model.Estudiante;
import com.example.service.EstudianteService;

public class Main {
    public static void main(String[] args) {

        System.out.println("\n==================================================");
        System.out.println("=== 1. Inicializando el Contenedor IoC de Spring ===");
        
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        System.out.println("\n=== 2. Solicitando el Bean de Servicio ===");

        EstudianteService estudianteService = (EstudianteService) context.getBean("estudianteServiceBean");

        System.out.println("\n======== 3. Ejecutando Logica de negocio ========");

        List<Estudiante> ListaEstudiantes = estudianteService.listarEstudiantes();

        Estudiante estudiante1 = ListaEstudiantes.get(0);
        Estudiante estudiante2 = ListaEstudiantes.get(1);

        System.out.println("\n==================================================");
        System.out.println("3.1. ATENDIENDO A: " + estudiante1.getNombre());
        System.out.println("==================================================");
        CarritoMatricula carritoEstudiante1 = context.getBean("carritoMatriculoBean", CarritoMatricula.class);
        carritoEstudiante1.asignarEstudiante(estudiante1);
        carritoEstudiante1.agregarCurso("Computación en Internet III");
        carritoEstudiante1.agregarCurso("Bases de Datos");

        System.out.println("Materias en el carrito del Estudiante 1: " + carritoEstudiante1.getCursos());

        System.out.println("\n==================================================");
        System.out.println("3.2. ATENDIENDO A: " + estudiante2.getNombre());
        System.out.println("==================================================");
        CarritoMatricula carritoEstudiante2 = context.getBean("carritoMatriculoBean", CarritoMatricula.class);
        carritoEstudiante2.asignarEstudiante(estudiante2);
        carritoEstudiante2.agregarCurso("Computación en Internet II");
        carritoEstudiante2.agregarCurso("Ingesoft IV");

        System.out.println("Materias en el carrito del Estudiante 2: " + carritoEstudiante2.getCursos());
        System.out.println("\n==================================================");
        System.out.println("3.3. REVISION FINAL");
        System.out.println("==================================================");
        System.out.println("¿El carrito de Ana y Carlos son la misma instancia en memoria?: " + (carritoEstudiante1 == carritoEstudiante2));

        System.out.println("\n=== 4. Cerrando el Contenedor IoC ===");
        System.out.println("==================================================");
        context.close();
    }
}
