package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SistemaConfigService {

    // 1. Inyección de cadenas de texto
    @Value("${app.institucion.nombre}")
    private String institucion;

    @Value("${app.institucion.departamento}")
    private String departamento;

    // 2. Conversión automática a tipos numéricos y booleanos
    @Value("${app.matricula.max-creditos}")
    private int maxCreditos;

    @Value("${app.matricula.costo-credito}")
    private double costoCredito;

    @Value("${app.matricula.descuento-beca}")
    private double descuentoBeca;

    @Value("${app.matricula.habilitada}")
    private boolean matriculaHabilitada;

    // 3. Valor con fallback / por defecto si la llave no existe en el properties
    @Value("${app.servidor.timeout:5000}")
    private int timeout;

    @Value("${app.correo.soporte:soporte-ti@icesi.edu.co}")
    private String correoSoporte;

    // Getters para permitir el acceso desde otros Beans y expresiones SpEL
    public String getInstitucion() { return institucion; }
    public String getDepartamento() { return departamento; }
    public int getMaxCreditos() { return maxCreditos; }
    public double getCostoCredito() { return costoCredito; }
    public double getDescuentoBeca() { return descuentoBeca; }
    public boolean isMatriculaHabilitada() { return matriculaHabilitada; }
    public int getTimeout() { return timeout; }
    public String getCorreoSoporte() { return correoSoporte; }
}