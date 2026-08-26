# Plan de Implementación Pedagógico: `@PropertySource`, `@Value` y Spring Expression Language (SpEL)

Este plan de implementación y guía docente está diseñado para integrar en el proyecto **`intro_spring`** los conceptos abordados a partir de la **página 18** de la presentación (`spring-annotations-spel.pdf`), orientándolo al dominio del proyecto: **Gestión Académica y Matrículas**.

---

## 🎯 Objetivos de Aprendizaje

Al finalizar la sesión, los estudiantes serán capaces de:
1. **Desacoplar la configuración** hardcodeada hacia archivos externos `.properties` usando `@PropertySource`.
2. **Inyectar valores de configuración** y tipos primitivos/objetos con `@Value("${...}")`, incluyendo valores por defecto.
3. **Comprender la diferencia crucial** entre la lectura pasiva (`${...}`) y la evaluación dinámica con SpEL (`#{...}`).
4. **Construir expresiones SpEL** que realicen:
   - Operaciones aritméticas y lógicas.
   - Invocación de métodos y propiedades de otros Beans del contenedor.
   - Uso de clases y utilidades estáticas de Java con el operador `T(...)`.
   - Operadores ternarios condicionales y expresiones anidadas `${}` dentro de `#{}`.

---

## 🏗️ Propuesta de Caso de Uso en el Proyecto

En nuestro sistema de estudiantes y carritos de matrícula (`intro_spring`), implementaremos:
1. **Archivo `application.properties`**: Parámetros globales de la institución, límites de créditos, costo por crédito, tasa de descuento y estado del periodo académico.
2. **`AppConfig`**: Configuración Java pura (`@Configuration`) con escaneo y carga de propiedades con `@PropertySource`.
3. **`SistemaConfigService`**: Componente que lee parámetros de configuración institucional con `@Value("${...}")`.
4. **`LiquidacionMatriculaService` / `AuditoriaMatriculaComponent`**: Componentes que aplican reglas dinámicas de liquidación y auditoría usando **SpEL** (`#{...}` y `T(...)`).
5. **`MainApplication`**: Demostración interactiva en consola mostrando los valores resueltos dinámicamente.

---

## 📋 Arquitectura de Componentes

```
intro_spring/
├── src/main/resources/
│   └── application.properties                      <-- [NUEVO] Configuración externa
└── src/main/java/com/example/
    ├── config/
    │   └── AppConfig.java                          <-- [MODIFICAR] @PropertySource y @ComponentScan
    ├── service/
    │   ├── SistemaConfigService.java               <-- [NUEVO] Inyección con @Value("${...}")
    │   └── LiquidacionMatriculaService.java        <-- [NUEVO] Cálculos y lógica con SpEL #{...}
    ├── model/
    │   └── AuditoriaMatricula.java (opcional) o Componente de auditoría
    └── MainApplication.java                       <-- [MODIFICAR] Demostración en consola con JavaConfig
```

---

## 🚀 Paso a Paso de la Implementación en Clase

### Paso 1: Crear el archivo de propiedades externas
**Archivo:** `src/main/resources/application.properties`

> **Propósito Pedagógico:** Mostrar a los estudiantes cómo evitar parámetros quemados en código (*hardcoded*), facilitando cambios de entorno sin recompilar la aplicación.

```properties
# ===============================================
# Configuración Institucional y de la Aplicación
# ===============================================
app.institucion.nombre=Universidad Icesi
app.institucion.departamento=Ingeniería de Sistemas
app.sistema.version=2.0-SpringCore
app.sistema.ambiente=PRODUCCION

# ===============================================
# Parámetros Financieros y de Matrícula
# ===============================================
app.matricula.max-creditos=20
app.matricula.costo-credito=480000
app.matricula.descuento-beca=0.15
app.matricula.habilitada=true
app.matricula.email-contacto=matriculas@icesi.edu.co
```

---

### Paso 2: Vincular el archivo de propiedades en `AppConfig`
**Archivo:** `src/main/java/com/example/config/AppConfig.java`

> **Concepto Diapositiva 18:** Activar `@Configuration`, `@ComponentScan` y anotar `@PropertySource("classpath:application.properties")`.

```java
package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "com.example")
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Bean
    public String nombreAplicacion() {
        return "Sistema de Gestión Académica y Matrículas Icesi";
    }
}
```

---

### Paso 3: Inyección de Propiedades con `@Value("${...}")`
**Archivo:** `src/main/java/com/example/service/SistemaConfigService.java`

> **Concepto Diapositivas 19 y 20:**
> - Inyección con sintaxis `${property.key}`.
> - Conversión automática de tipos (`String`, `int`, `double`, `boolean`).
> - Manejo de valores por defecto con sintaxis `${propiedad:valorPorDefecto}`.

```java
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
```

---

### Paso 4: Implementar SpEL (Spring Expression Language)
**Archivo:** `src/main/java/com/example/service/LiquidacionMatriculaService.java`

> **Conceptos Diapositivas 21 a 28:**
> - Evaluación dinámica con `#{...}`.
> - Operaciones aritméticas y lógicas.
> - Acceso a métodos y propiedades de otros beans (`sistemaConfigService.costoCredito`).
> - Invocación de clases y métodos estáticos con `T(...)` (`java.lang.Math`, `java.util.UUID`).
> - Operador ternario (`condicion ? valorTrue : valorFalse`).
> - Combinación anidada de `${...}` dentro de `#{...}`.

```java
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
```

---

### Paso 5: Demostración en `MainApplication`
**Archivo:** `src/main/java/com/example/MainApplication.java`

> **Concepto Diapositiva 12:** Inicializar el contenedor IoC mediante `AnnotationConfigApplicationContext(AppConfig.class)`.

```java
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
```

---

## 📊 Matriz Comparativa: Property Placeholder `${...}` vs SpEL `#{...}`

Compartir esta tabla con los estudiantes para aclarar una de las confusiones más comunes:

| Característica | Property Placeholder `${...}` | SpEL Expression `#{...}` |
| :--- | :--- | :--- |
| **Objetivo principal** | Leer texto literal desde un archivo `.properties` o variables de entorno. | Evaluar expresiones lógicas, matemáticas y de objetos en runtime. |
| **¿Realiza cálculos?** | ❌ No (se interpreta como texto literal). | ✅ Sí (`#{10 * 5 + 20}`). |
| **¿Llama métodos de Beans?** | ❌ No. | ✅ Sí (`#{miBean.metodo()}`). |
| **¿Invoca métodos estáticos?**| ❌ No. | ✅ Sí (`#{T(java.lang.Math).random()}`). |
| **¿Soporta valor por defecto?**| ✅ Sí (`${clave:defecto}`). | ✅ Mediante el operador Elvis `?:` (`#{bean.valor ?: 'default'}`). |
| **Combinación** | No puede contener SpEL. | ✅ Puede contener un `${...}` adentro (`#{${app.precio} * 1.19}`). |

---

## ⚠️ Errores Típicos a Demostrar en Clase (Live Debugging)

1. **Olvidar `@PropertySource` en `AppConfig`**:
   - *Qué pasa:* `@Value("${app.nombre}")` inyecta la cadena literal `"${app.nombre}"` en lugar del valor del archivo properties.
2. **Confundir la sintaxis `${}` y `#{}`**:
   - *Error común del estudiante:* `@Value("${10 * 5}")`.
   - *Resultado:* Spring intenta buscar una llave llamada literalmente `"10 * 5"` en el archivo properties y lanza excepción `IllegalArgumentException: Could not resolve placeholder`.
3. **Olvidar la `T()` al invocar clases estáticas**:
   - *Error:* `#{java.lang.Math.PI}` en vez de `#{T(java.lang.Math).PI}`.
   - *Resultado:* `SpelEvaluationException: EL1004E: Type cannot be found`.
4. **Dependencia del nombre de los Beans en SpEL**:
   - Spring nombra los beans en *camelCase* por defecto (`sistemaConfigService`). Si el estudiante escribe `#{SistemaConfigService.get...}` con mayúscula, SpEL fallará buscando un bean con ese nombre.

---

## 💡 Retos Propuestos para los Estudiantes en Clase

1. **Reto 1 (SpEL + Ternario):**
   Agregar al archivo properties la propiedad `app.estudiante.promedio-minimo-honor=4.5`. En un servicio, evaluar mediante SpEL si un promedio ficticio (ej. `4.7`) otorga la distinción `"Candidato a Grado de Honor"` o `"Grado Regular"`.
2. **Reto 2 (Operador Elvis `?:` de SpEL):**
   Demostrar el operador Elvis de SpEL: `#{sistemaConfigService.departamento ?: 'Departamento General'}` para asignar un valor por defecto si el método retorna `null`.
3. **Reto 3 (SpEL con Fechas / LocalDate):**
   Inyectar el año o fecha actual usando la clase estática de Java Time:
   `@Value("#{T(java.time.Year).now().getValue()}") int anioActual;`
