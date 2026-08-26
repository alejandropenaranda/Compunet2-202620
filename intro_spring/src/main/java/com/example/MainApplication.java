package com.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import com.example.config.AppConfig;
import com.example.service.LiquidacionMatriculaService;

public class MainApplication {
    
    public static void main(String[] args) {
        System.out.println("Iniciando contenedor Spring con JavaConfig y SpEL...\n");

        // 1. Inicializar el ApplicationContext usando la clase @Configuration
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);

        // 2. Obtener el servicio con las expresiones SpEL inyectadas
        LiquidacionMatriculaService liquidacionService = 
                context.getBean(LiquidacionMatriculaService.class);

        // 3. Ejecutar y visualizar los resultados
        liquidacionService.imprimirReporteLiquidacion();

        // 4. Cerrar el contexto de Spring
        context.close();
    }
}