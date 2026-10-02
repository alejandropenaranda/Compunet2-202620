-- ==============================================================================
-- INSERTS BASE DEL SISTEMA ACADÉMICO (Ejecución automática al inicio)
-- ==============================================================================

-- ------------------------------------------------------------------------------
-- 1. PERMISOS DEL SISTEMA (RBAC)
-- ------------------------------------------------------------------------------
INSERT INTO permiso (nombre, descripcion) VALUES ('USER_CREATE', 'Permite crear nuevos usuarios en el sistema');
INSERT INTO permiso (nombre, descripcion) VALUES ('USER_READ', 'Permite consultar información de usuarios');
INSERT INTO permiso (nombre, descripcion) VALUES ('USER_UPDATE', 'Permite actualizar datos de usuarios');
INSERT INTO permiso (nombre, descripcion) VALUES ('USER_DELETE', 'Permite eliminar usuarios del sistema');
INSERT INTO permiso (nombre, descripcion) VALUES ('COURSE_READ', 'Permite consultar cursos académicos');
INSERT INTO permiso (nombre, descripcion) VALUES ('COURSE_WRITE', 'Permite crear y actualizar cursos académicos');
INSERT INTO permiso (nombre, descripcion) VALUES ('ENROLLMENT_WRITE', 'Permite matricular estudiantes en cursos');

-- ------------------------------------------------------------------------------
-- 2. ROLES DE SEGURIDAD
-- ------------------------------------------------------------------------------
INSERT INTO rol (nombre, descripcion) VALUES ('ADMINISTRADOR', 'Administrador con acceso y privilegios totales');
INSERT INTO rol (nombre, descripcion) VALUES ('PROFESOR', 'Docente con permisos de gestión académica y cursos');
INSERT INTO rol (nombre, descripcion) VALUES ('ESTUDIANTE', 'Estudiante con permisos de consulta y matrícula');

-- ------------------------------------------------------------------------------
-- 3. ASIGNACIÓN ROL <-> PERMISO (rol_permiso)
-- ------------------------------------------------------------------------------
-- ADMINISTRADOR (rol_id = 1)
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 1); -- USER_CREATE
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 2); -- USER_READ
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 3); -- USER_UPDATE
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 4); -- USER_DELETE
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 5); -- COURSE_READ
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 6); -- COURSE_WRITE
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (1, 7); -- ENROLLMENT_WRITE

-- PROFESOR (rol_id = 2)
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (2, 2); -- USER_READ
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (2, 5); -- COURSE_READ
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (2, 6); -- COURSE_WRITE

-- ESTUDIANTE (rol_id = 3)
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (3, 5); -- COURSE_READ
INSERT INTO rol_permiso (rol_id, permiso_id) VALUES (3, 7); -- ENROLLMENT_WRITE

-- ------------------------------------------------------------------------------
-- 4. USUARIOS DEL SISTEMA (Total: 27 usuarios = 2 Admin + 10 Docentes + 15 Estudiantes)
-- ------------------------------------------------------------------------------

-- Administradores (IDs: 1, 2)
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Super', 'Admin', 'admin@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Laura', 'Gomez', 'lgomez@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);

-- Docentes (IDs: 3 a 12)
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Domiciano', 'Rincon', 'drincon@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Kevin', 'Rodriguez', 'krodriguez@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Alejandro', 'Munoz', 'amunoz@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Alejandro', 'Penaranda', 'apenaranda@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Beatriz', 'Caicedo', 'bcaicedo@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Gonzalo', 'Ulloa', 'gulloa@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Andres', 'Paredes', 'aparedes@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Claudia', 'Jimenez', 'cjimenez@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Mauricio', 'Cabrera', 'mcabrera@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Paola', 'Vallejo', 'pvallejo@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);

-- Estudiantes (IDs: 13 a 27)
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Alejandro', 'Paez', 'apaez@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Carlos', 'Perez', 'cperez@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Raul', 'Martinez', 'rmartinez@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Sofia', 'Castillo', 'scastillo@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Mateo', 'Ospina', 'mospina@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Valentina', 'Herrera', 'vherrera@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Santiago', 'Morales', 'smorales@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Isabella', 'Rios', 'irios@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Daniel', 'Torres', 'dtorres@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Mariana', 'Vargas', 'mvargas@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Gabriel', 'Mendoza', 'gmendoza@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Camila', 'Silva', 'csilva@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Nicolas', 'Cruz', 'ncruz@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Daniela', 'Rojas', 'drojas@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);
INSERT INTO usuario (nombre, apellido, correo_institucional, password, active) VALUES ('Felipe', 'Aguilar', 'faguilar@icesi.edu.co', '$2a$10$hashedpassword123', TRUE);

-- ------------------------------------------------------------------------------
-- 5. ASIGNACIÓN USUARIO <-> ROL (usuario_rol)
-- ------------------------------------------------------------------------------
-- Admins (rol_id = 1)
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (1, 1);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (2, 1);

-- Docentes (rol_id = 2)
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (3, 2);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (4, 2);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (5, 2);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (6, 2);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (7, 2);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (8, 2);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (9, 2);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (10, 2);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (11, 2);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (12, 2);

-- Estudiantes (rol_id = 3)
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (13, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (14, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (15, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (16, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (17, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (18, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (19, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (20, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (21, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (22, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (23, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (24, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (25, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (26, 3);
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES (27, 3);

-- ------------------------------------------------------------------------------
-- 6. PERFILES DE PROFESORES (10 Docentes con especialidad y departamento)
-- Departamentos:
--   - Computación y Sistemas Inteligentes (TIC)
--   - Ciencias Básicas y Matemáticas
--   - Ingeniería Industrial y Gestión
--   - Diseño y Medios Digitales
-- ------------------------------------------------------------------------------
INSERT INTO profesor (usuario_id, especialidad, departamento) VALUES (3, 'Telemática y Redes de Computadores', 'Computación y Sistemas Inteligentes');
INSERT INTO profesor (usuario_id, especialidad, departamento) VALUES (4, 'Desarrollo de Software y Cloud Computing', 'Computación y Sistemas Inteligentes');
INSERT INTO profesor (usuario_id, especialidad, departamento) VALUES (5, 'Arquitectura de Software y Patrones', 'Computación y Sistemas Inteligentes');
INSERT INTO profesor (usuario_id, especialidad, departamento) VALUES (6, 'Desarrollo Web Fullstack y Microservicios', 'Computación y Sistemas Inteligentes');
INSERT INTO profesor (usuario_id, especialidad, departamento) VALUES (7, 'Inteligencia Artificial y Machine Learning', 'Computación y Sistemas Inteligentes');
INSERT INTO profesor (usuario_id, especialidad, departamento) VALUES (8, 'Cálculo Avanzado y Álgebra Lineal', 'Ciencias Básicas y Matemáticas');
INSERT INTO profesor (usuario_id, especialidad, departamento) VALUES (9, 'Probabilidad y Estadística Aplicada', 'Ciencias Básicas y Matemáticas');
INSERT INTO profesor (usuario_id, especialidad, departamento) VALUES (10, 'Optimización y Modelos de Operaciones', 'Ingeniería Industrial y Gestión');
INSERT INTO profesor (usuario_id, especialidad, departamento) VALUES (11, 'Gestión Estratégica y Gerencia de Proyectos TIC', 'Ingeniería Industrial y Gestión');
INSERT INTO profesor (usuario_id, especialidad, departamento) VALUES (12, 'Diseño de Experiencia de Usuario (UI/UX)', 'Diseño y Medios Digitales');

-- ------------------------------------------------------------------------------
-- 7. CURSOS ACADÉMICOS (Asignaturas distribuidas en los 4 departamentos)
-- ------------------------------------------------------------------------------
-- Cursos TIC / Computación y Sistemas Inteligentes (profesor_id: 1 a 5)
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Desarrollo de aplicaciones móviles', 2, 'Computación y Sistemas Inteligentes', 1);
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Computación en internet 3', 4, 'Computación y Sistemas Inteligentes', 2);
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Ingeniería de Software 4', 3, 'Computación y Sistemas Inteligentes', 3);
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Computación en internet 2', 3, 'Computación y Sistemas Inteligentes', 4);
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Inteligencia Artificial y Redes Neuronales', 4, 'Computación y Sistemas Inteligentes', 5);
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Arquitectura Empresarial y Cloud', 3, 'Computación y Sistemas Inteligentes', 3);

-- Cursos Ciencias Básicas y Matemáticas (profesor_id: 6, 7)
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Cálculo Multivariado', 3, 'Ciencias Básicas y Matemáticas', 6);
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Álgebra Lineal Computacional', 3, 'Ciencias Básicas y Matemáticas', 6);
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Probabilidad y Estadística para Ingenieros', 3, 'Ciencias Básicas y Matemáticas', 7);

-- Cursos Ingeniería Industrial y Gestión (profesor_id: 8, 9)
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Investigación de Operaciones I', 3, 'Ingeniería Industrial y Gestión', 8);
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Gerencia de Proyectos Ágiles', 2, 'Ingeniería Industrial y Gestión', 9);

-- Cursos Diseño y Medios Digitales (profesor_id: 10)
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Diseño de Interfaces y Experiencia de Usuario', 3, 'Diseño y Medios Digitales', 10);
INSERT INTO curso (nombre, creditos, departamento, profesor_id) VALUES ('Prototipado Digital e Interacción Humano-Computador', 2, 'Diseño y Medios Digitales', 10);

-- ------------------------------------------------------------------------------
-- 8. MATRÍCULAS DE ESTUDIANTES EN CURSOS (matricula: usuario_id, curso_id)
-- ------------------------------------------------------------------------------
-- Estudiante 13 (Alejandro Paez)
INSERT INTO matricula (usuario_id, curso_id) VALUES (13, 1);
INSERT INTO matricula (usuario_id, curso_id) VALUES (13, 2);
INSERT INTO matricula (usuario_id, curso_id) VALUES (13, 4);

-- Estudiante 14 (Carlos Perez)
INSERT INTO matricula (usuario_id, curso_id) VALUES (14, 1);
INSERT INTO matricula (usuario_id, curso_id) VALUES (14, 2);
INSERT INTO matricula (usuario_id, curso_id) VALUES (14, 3);

-- Estudiante 15 (Raul Martinez)
INSERT INTO matricula (usuario_id, curso_id) VALUES (15, 2);
INSERT INTO matricula (usuario_id, curso_id) VALUES (15, 3);
INSERT INTO matricula (usuario_id, curso_id) VALUES (15, 4);

-- Estudiante 16 (Sofia Castillo)
INSERT INTO matricula (usuario_id, curso_id) VALUES (16, 4);
INSERT INTO matricula (usuario_id, curso_id) VALUES (16, 5);
INSERT INTO matricula (usuario_id, curso_id) VALUES (16, 9);

-- Estudiante 17 (Mateo Ospina)
INSERT INTO matricula (usuario_id, curso_id) VALUES (17, 1);
INSERT INTO matricula (usuario_id, curso_id) VALUES (17, 7);
INSERT INTO matricula (usuario_id, curso_id) VALUES (17, 8);

-- Estudiante 18 (Valentina Herrera)
INSERT INTO matricula (usuario_id, curso_id) VALUES (18, 5);
INSERT INTO matricula (usuario_id, curso_id) VALUES (18, 11);
INSERT INTO matricula (usuario_id, curso_id) VALUES (18, 12);

-- Estudiante 19 (Santiago Morales)
INSERT INTO matricula (usuario_id, curso_id) VALUES (19, 3);
INSERT INTO matricula (usuario_id, curso_id) VALUES (19, 6);
INSERT INTO matricula (usuario_id, curso_id) VALUES (19, 10);

-- Estudiante 20 (Isabella Rios)
INSERT INTO matricula (usuario_id, curso_id) VALUES (20, 2);
INSERT INTO matricula (usuario_id, curso_id) VALUES (20, 4);
INSERT INTO matricula (usuario_id, curso_id) VALUES (20, 11);
