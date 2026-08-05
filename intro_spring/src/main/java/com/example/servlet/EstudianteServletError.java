package com.example.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.example.model.Estudiante;
import com.example.service.EstudianteService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/estudiantesE")
public class EstudianteServletError  extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest reques, HttpServletResponse response)
            throws ServletException, IOException {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        EstudianteService estudianteService = (EstudianteService) context.getBean("estudianteServiceBean");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        List<Estudiante> estudiantes = estudianteService.listarEstudiantes();

        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Gestión de Estudiantes - Spring + Servlets</title></head><body>");
        out.println("<h1>Gestión de Estudiantes (Spring Context + Servlets)</h1>");

        // Formulario HTML para registrar un nuevo estudiante vía POST
        out.println("<h2>Registrar Nuevo Estudiante</h2>");
        out.println("<form action='/intro_spring/estudiantesE' method='POST'>");
        out.println("  <label>Nombre:</label><br/>");
        out.println("  <input type='text' name='nombre' required/><br/><br/>");
        out.println("  <label>Correo:</label><br/>");
        out.println("  <input type='email' name='correo' required/><br/><br/>");
        out.println("  <button type='submit'>Guardar Estudiante</button>");
        out.println("</form>");

        out.println("<hr/>");
        out.println("<h2>Lista de Estudiantes Registrados</h2>");
        out.println("<ul>");
        for (Estudiante e : estudiantes) {
            out.println("<li><strong>" + e.getNombre() + "</strong> - " + e.getCorreo() + "</li>");
        }
        out.println("</ul>");
        out.println("</body></html>");

        context.close();
    }

        // 2. Método POST: Recibe los datos del formulario, invoca la Capa de Servicio y redirige
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        EstudianteService estudianteService = (EstudianteService) context.getBean("estudianteServiceBean");

        // Extraer parámetros enviados por el formulario HTML
        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");

        // Crear el objeto del Modelo de Dominio
        String idGenerado = UUID.randomUUID().toString().substring(0, 8);
        Estudiante nuevoEstudiante = new Estudiante(idGenerado, nombre, correo);

        // Delegar la ejecución a la Capa de Servicio (Spring Bean)
        estudianteService.registrarEstudiante(nuevoEstudiante);

        // Redireccionar (Pattern Post-Redirect-Get) para refrescar la lista
        response.sendRedirect(request.getContextPath() + "/estudiantesE");

        context.close();
    }

}
