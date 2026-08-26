# 🎓 Guía de Retos Prácticos en Clase: `@Value`, `@PropertySource` y SpEL

> **Público Objetivo:** Estudiantes de Computación en Internet II (Universidad Icesi)  
> **Requisito Previo:** Haber completado la explicación magistral hasta el **Paso 5** del plan de implementación con `SistemaConfigService`, `LiquidacionMatriculaService` y ejecución en `MainApplication`.

---

## 🎯 Objetivos de la Actividad Práctica

1. Afianzar el uso de inyección de propiedades externas y valores por defecto con `@Value("${...}")`.
2. Dominar la construcción de expresiones dinámicas en tiempo de ejecución con **Spring Expression Language (`#{...}`)**.
3. Aprender a combinar `${}` dentro de `#{}` y a interactuar con métodos de Java estándar (`T(...)`) y beans del contenedor Spring.
4. Desarrollar intuición para resolver errores comunes de configuración e inyección.

---

## 🏆 Nivel 1: Retos Básicos (Calentamiento)

### Reto 1.1: Generador Dinámico de Correo Institucional
* **Contexto de Negocio:** El sistema necesita asignar automáticamente un alias institucional a los correos de soporte y generar un código de auditoría para el ciclo lectivo actual.
* **Objetivo Técnico:** Manejo de manipulación de Strings en SpEL y fallback con `${...:default}`.
* **Instrucciones:**
  1. En `application.properties`, define la propiedad `app.institucion.dominio-correo=icesi.edu.co`.
  2. En un nuevo bean `@Service` llamado `EstudianteAuditoriaService` (o dentro de un servicio a tu elección):
     - Inyecta la propiedad `app.institucion.sigla` con valor por defecto `"ICESI"`.
     - Usa SpEL para generar el correo de bienvenida del estudiante `"juan.perez"` concatenándolo con `@` y el dominio configurado en minúsculas.
* **Salida esperada:**
  ```text
  Correo de prueba generado: juan.perez@icesi.edu.co
  Sigla Institucional: ICESI
  ```

---

### Reto 1.2: Inyección de Fecha y Año Actual con SpEL `T(...)`
* **Contexto de Negocio:** Toda liquidación de matrícula debe registrar el año fiscal y la fecha/hora exacta en la que se generó la transacción sin instanciar manualmente objetos `new Date()` en el código de negocio.
* **Objetivo Técnico:** Uso del operador `T(...)` con la API de fechas moderna de Java (`java.time.LocalDate` y `java.time.Year`).
* **Instrucciones:**
  1. En tu servicio, declara e inyecta usando SpEL:
     - `int anioLectivo` utilizando `T(java.time.Year).now().getValue()`.
     - `String fechaGeneracion` formateada o como texto utilizando `T(java.time.LocalDate).now().toString()`.
* **Criterio de Aceptación:** Al arrancar el contexto de Spring, el servicio debe mostrar el año en curso y la fecha de hoy.

---

## 🚀 Nivel 2: Retos Intermedios (Lógica de Negocio y Beans)

### Reto 2.1: Clasificación de Estudiantes por Promedio (Operador Ternario)
* **Contexto de Negocio:** La universidad otorga automáticamente el estado de *"Candidato a Grado de Honor"* a quienes superen un umbral de promedio definido institucionalmente.
* **Objetivo Técnico:** Uso de operadores relacionales (`>=`), ternarios (`? :`) y anidamiento de propiedades `${}` dentro de SpEL `#{}`.
* **Instrucciones:**
  1. Agrega a `application.properties`:
     ```properties
     app.academico.nota-minima-honor=4.5
     ```
  2. Supongamos un promedio de prueba evaluado de un estudiante (por ejemplo `4.7`).
  3. Crea una expresión SpEL que compare una nota fija o referenciada contra la propiedad `${app.academico.nota-minima-honor}`:
     - Si la nota es $\ge$ umbral $\rightarrow$ `"CANDIDATO A GRADO DE HONOR"`.
     - Si no $\rightarrow$ `"ESTADO REGULAR"`.
* **Pista de sintaxis:**
  ```java
  @Value("#{4.7 >= ${app.academico.nota-minima-honor} ? 'CANDIDATO A GRADO DE HONOR' : 'ESTADO REGULAR'}")
  private String estadoHonor;
  ```

---

### Reto 2.2: Operador Elvis `?:` y Manejo Seguro de Nulos
* **Contexto de Negocio:** Si un departamento o dependencia institucional no está configurada (retorna `null` o vacío), el sistema debe asignar automáticamente `"Sede Principal / Sin Asignar"` sin que se produzca un `NullPointerException`.
* **Objetivo Técnico:** Uso del operador Elvis (`?:`) nativo de SpEL.
* **Instrucciones:**
  1. En `SistemaConfigService`, añade un método `public String getSedeOpcional() { return null; }` (simulando un valor no configurado).
  2. En el servicio de liquidación o auditoría, inyecta con SpEL el resultado de llamar `getSedeOpcional()`, aplicando el operador Elvis para proveer el texto por defecto.
* **Pista:** `#{sistemaConfigService.sedeOpcional ?: 'Sede Principal - Cali'}`

---

## 🔥 Nivel 3: Retos Avanzados (Integración Completa)

### Reto 3.1: Cotizador de Créditos con Recargo por Créditos Extra
* **Contexto de Negocio:** 
  - La matrícula base cubre hasta `app.matricula.max-creditos` (ej. 20 créditos).
  - Si un estudiante desea matricular créditos adicionales (ej. 23 créditos), los 3 créditos extra tienen un recargo del **25% adicional** sobre el costo base del crédito.
* **Objetivo Técnico:** Operaciones aritméticas compuestas, anidamiento de propiedades y llamadas a beans.
* **Instrucciones:**
  1. En `application.properties`:
     ```properties
     app.matricula.recargo-extra=0.25
     ```
  2. Implementa una expresión SpEL para calcular el costo total de un estudiante con **24 créditos** (20 créditos a tarifa base + 4 créditos a tarifa con recargo del 25%):
     $$\text{Costo Total} = (20 \times \text{costoBase}) + (4 \times (\text{costoBase} \times 1.25))$$
  3. Ejecútalo y muestra el desglose financiero en consola.

---

### Reto 3.2: Generador de Token Seguro de Transacción
* **Contexto de Negocio:** El sistema bancario asociado exige que cada liquidación lleve una firma digital con el formato:
  `REC-<SIGLA_DEPTO>-<UUID_8_CHARS>-<TIMESTAMP>`
* **Objetivo Técnico:** Concatenación compleja, invocación de métodos de bean y múltiples utilidades de `T(...)`.
* **Instrucciones:**
  1. Construye una expresión SpEL que ensamble:
     - El prefijo literal `'REC-'`
     - Los primeros 4 caracteres en mayúscula del departamento (`#{sistemaConfigService.departamento.substring(0,4).toUpperCase()}`)
     - Un subconjunto de un UUID aleatorio (`#{T(java.util.UUID).randomUUID().toString().substring(0,8)}`)
     - El timestamp actual (`#{T(java.lang.System).currentTimeMillis()}`)
* **Resultado esperado:** Un token con estructura similar a `REC-INGE-7F8A12B4-1724683921000`.

---

## ⚡ Desafío de Depuración en Vivo ("Encuentra el Bug")

Pide a los estudiantes que analicen estos 3 fragmentos de código e indiquen **por qué fallan** antes de compilar:

### 🐛 Caso A:
```java
@Value("${150000 * 2}")
private double total;
```
> **Pregunta:** ¿Qué excepción lanzará Spring al iniciar? ¿Cómo se soluciona?

### 🐛 Caso B:
```java
@Value("#{java.lang.Math.random() > 0.5 ? 'ACTIVO' : 'INACTIVO'}")
private String estado;
```
> **Pregunta:** ¿Por qué falla al evaluar la clase `Math`?

### 🐛 Caso C:
```java
@Value("#{SistemaConfigService.costoCredito * 2}")
private double costoDoble;
```
> **Pregunta:** Asumiendo que la clase se llama `SistemaConfigService`, ¿por qué falla en runtime?

---

## 📖 Solucionario de Referencia (Para el Docente)

<details>
<summary><b>👁️ Click para ver las soluciones de los retos y casos de depuración</b></summary>

### Solución Caso A (Depuración):
* **Causa:** Se usó `${...}` en lugar de `#{...}`. Spring busca una propiedad llamada `"150000 * 2"` en `application.properties` y lanza `IllegalArgumentException: Could not resolve placeholder`.
* **Corrección:** `@Value("#{150000 * 2}")`

### Solución Caso B (Depuración):
* **Causa:** En SpEL, las clases estáticas deben envolverse con el operador `T(...)`.
* **Corrección:** `@Value("#{T(java.lang.Math).random() > 0.5 ? 'ACTIVO' : 'INACTIVO'}")`

### Solución Caso C (Depuración):
* **Causa:** Por convención, los nombres de los Beans registrados por `@Service` o `@Component` inician con minúscula (*camelCase*).
* **Corrección:** `@Value("#{sistemaConfigService.costoCredito * 2}")`

### Código de Solución Integrada de los Retos:
```java
package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RetosEstudiantesService {

    // Reto 1.1: Correo dinámico y default
    @Value("${app.institucion.sigla:ICESI}")
    private String siglaInstitucion;

    @Value("#{'juan.perez@' + '${app.institucion.dominio-correo:icesi.edu.co}'.toLowerCase()}")
    private String correoGenerado;

    // Reto 1.2: Fechas con T(...)
    @Value("#{T(java.time.Year).now().getValue()}")
    private int anioLectivo;

    @Value("#{T(java.time.LocalDate).now().toString()}")
    private String fechaHoy;

    // Reto 2.1: Ternario y comparación
    @Value("#{4.7 >= ${app.academico.nota-minima-honor:4.5} ? 'CANDIDATO A GRADO DE HONOR' : 'ESTADO REGULAR'}")
    private String estadoHonor;

    // Reto 3.2: Token Compuesto
    @Value("#{'REC-' + sistemaConfigService.departamento.substring(0,4).toUpperCase() + '-' + T(java.util.UUID).randomUUID().toString().substring(0,8).toUpperCase() + '-' + T(java.lang.System).currentTimeMillis()}")
    private String tokenTransaccion;

    public void mostrarResultadosRetos() {
        System.out.println("=== RESULTADOS DE LOS RETOS ===");
        System.out.println("Sigla: " + siglaInstitucion);
        System.out.println("Correo: " + correoGenerado);
        System.out.println("Año Lectivo: " + anioLectivo + " | Fecha: " + fechaHoy);
        System.out.println("Estado Académico: " + estadoHonor);
        System.out.println("Token Seguro: " + tokenTransaccion);
    }
}
```
</details>
