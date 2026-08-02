package com.example;

import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.example.model.Estudiante;
import com.example.service.EstudianteService;

public class Main {
    public static void main(String[] args) {
        
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        EstudianteService estudianteService = (EstudianteService) context.getBean("estudianteServiceBean");

        List<Estudiante> estudiantes = estudianteService.listarEstudiantes();
        for (Estudiante estudiante : estudiantes) {
            System.out.println("Estudiante registrado:" + "ID: " + estudiante.getId() + 
            ", Nombre: " + estudiante.getNombre() + ", Correo: " + estudiante.getCorreo());
        }
        context.close();
    }
}
