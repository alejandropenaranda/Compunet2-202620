package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LiquidacionMatriculaService {

    // 1. SpEL Aritmético directo y manipulación de Strings (Slides 23, 24)
    @Value("#{10 * 5 + 20}")
    private int calculoEjemplo; // 70

    @Value("#{'portal académico'.toUpperCase() + ' - VERSIÓN 2026'}")
    private String tituloSistema;

    // 2. Acceso a Beans del ApplicationContext (Slide 25)
    // Invoca directamente el método getInstitucion() del bean 'sistemaConfigService'
    @Value("#{sistemaConfigService.institucion}")
    private String institucionReferenciada;

    @Value("#{sistemaConfigService.costoCredito * sistemaConfigService.maxCreditos}")
    private double costoMaximoSemestre;

    // 3. SpEL con Clases Estáticas de Java con el operador T(...) (Slide 26)
    @Value("#{T(java.lang.Math).PI}")
    private double valorPi;

    @Value("#{T(java.lang.Math).round(sistemaConfigService.costoCredito * 1.19)}")
    private long costoCreditoConIva;

    @Value("#{'TX-' + T(java.util.UUID).randomUUID().toString().substring(0, 8).toUpperCase()}")
    private String transaccionId;

    // 4. Operador Ternario y Lógica Relacional/Booleana (Slide 27, 28)
    @Value("#{sistemaConfigService.maxCreditos > 18 ? 'JORNADA INTENSIVA' : 'JORNADA REGULAR'}")
    private String tipoJornada;

    @Value("#{sistemaConfigService.matriculaHabilitada and sistemaConfigService.maxCreditos > 0 ? 'SISTEMA OPERATIVO' : 'SISTEMA CERRADO'}")
    private String estadoOperacional;

    // 5. Expresiones Anidadas: Combinando ${} de properties dentro de SpEL #{} (Slide 27)
    // Aplica el descuento configurado directamente al costo del crédito
    @Value("#{${app.matricula.costo-credito} * (1.0 - ${app.matricula.descuento-beca})}")
    private double costoCreditoConDescuento;

    // Inyección estándar por constructor del servicio de configuración para lógica de métodos
    private final SistemaConfigService configService;

    @Autowired
    public LiquidacionMatriculaService(SistemaConfigService configService) {
        this.configService = configService;
    }

    public void imprimirReporteLiquidacion() {
        System.out.println("\n========================================================");
        System.out.println("🎓 REPORTE DE LIQUIDACIÓN Y CONFIGURACIÓN DINÁMICA (SpEL)");
        System.out.println("========================================================");
        System.out.println("📌 Título del Sistema (SpEL String): " + tituloSistema);
        System.out.println("🏢 Institución (SpEL Bean Reference): " + institucionReferenciada);
        System.out.println("🆔 ID Transacción Generado (SpEL T(UUID)): " + transaccionId);
        System.out.println("💰 Costo Crédito Base ($): " + configService.getCostoCredito());
        System.out.println("💵 Costo Crédito con Descuento Beca (SpEL anidado): $" + costoCreditoConDescuento);
        System.out.println("🧾 Costo Crédito con IVA 19% (SpEL T(Math).round): $" + costoCreditoConIva);
        System.out.println("📚 Costo Máximo Semestre (20 créditos) (SpEL Bean Calc): $" + costoMaximoSemestre);
        System.out.println("📋 Clasificación de Jornada (SpEL Ternario): " + tipoJornada);
        System.out.println("🚦 Estado Operativo (SpEL Lógico): " + estadoOperacional);
        System.out.println("⏱️ Timeout Servidor (Default Value): " + configService.getTimeout() + " ms");
        System.out.println("📧 Soporte TI: " + configService.getCorreoSoporte());
        System.out.println("========================================================\n");
    }
}