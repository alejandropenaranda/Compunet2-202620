-- Inserts base de las tablas - Se ejecuta automaticamente al iniciar el proyecto --
INSERT INTO Estudiante (nombre, apellido, correo_institucional, active) VALUES ('Alejandro', 'Paez', 'apaez@icesi.edu.co', TRUE);
INSERT INTO Estudiante (nombre, apellido, correo_institucional, active) VALUES ('Carlos', 'Perez', 'cperez@icesi.edu.co', TRUE);
INSERT INTO Estudiante (nombre, apellido, correo_institucional, active) VALUES ('Raul', 'Martinez', 'rmartinez@icesi.edu.co', TRUE);

INSERT INTO Estudiante_Many (nombre, apellido, correo_institucional, active) VALUES ('Alejandro', 'Paez', 'apaez@icesi.edu.co', TRUE);
INSERT INTO Estudiante_Many (nombre, apellido, correo_institucional, active) VALUES ('Carlos', 'Perez', 'cperez@icesi.edu.co', TRUE);
INSERT INTO Estudiante_Many (nombre, apellido, correo_institucional, active) VALUES ('Raul', 'Martinez', 'rmartinez@icesi.edu.co', TRUE);

INSERT INTO Profesor (nombre, apellido, correo_institucional, especialidad, departamento, active) VALUES ('Domiciano', 'Rincon', 'drincon@icesi.edu.co', 'Telematica', 'Computación y Sistemas Inteligentes', TRUE);
INSERT INTO Profesor (nombre, apellido, correo_institucional, especialidad, departamento, active) VALUES ('Kevin', 'Rodriguez', 'krodriguez@icesi.edu.co', 'Desarrollo de software', 'Computación y Sistemas Inteligentes', TRUE);
INSERT INTO Profesor (nombre, apellido, correo_institucional, especialidad, departamento, active) VALUES ('Alejandro', 'Munoz', 'amunoz@icesi.edu.co', 'Arquitectura de Software', 'Computación y Sistemas Inteligentes', TRUE);
INSERT INTO Profesor (nombre, apellido, correo_institucional, especialidad, departamento, active) VALUES ('Alejandro', 'Peñaranda', 'apenaranda@icesi.edu.co', 'Desarrollo de software', 'Computación y Sistemas Inteligentes', TRUE);


-- INSERT INTO Curso (nombre, creditos, departamento, profesor_id) VALUES (NULL, 2, 'Computación y Sistemas Inteligentes', 1);
INSERT INTO Curso (nombre, creditos, departamento, profesor_id) VALUES ('Desarrollo de aplicaciones moviles', 2, 'Computación y Sistemas Inteligentes', 1);
INSERT INTO Curso (nombre, creditos, departamento, profesor_id) VALUES ('Computacion en internet 3', 4, 'Computación y Sistemas Inteligentes', 2);
INSERT INTO Curso (nombre, creditos, departamento, profesor_id) VALUES ('Ingenieria de Software 4', 3, 'Computación y Sistemas Inteligentes', 3);
INSERT INTO Curso (nombre, creditos, departamento, profesor_id) VALUES ('Computacion en internet 2', 3, 'Computación y Sistemas Inteligentes', 4);

INSERT INTO Curso_Many (nombre, creditos, departamento, profesor_id) VALUES ('Desarrollo de aplicaciones moviles', 2, 'Computación y Sistemas Inteligentes', 1);
INSERT INTO Curso_Many (nombre, creditos, departamento, profesor_id) VALUES ('Computacion en internet 3', 4, 'Computación y Sistemas Inteligentes', 2);
INSERT INTO Curso_Many (nombre, creditos, departamento, profesor_id) VALUES ('Ingenieria de Software 4', 3, 'Computación y Sistemas Inteligentes', 3);
INSERT INTO Curso_Many (nombre, creditos, departamento, profesor_id) VALUES ('Computacion en internet 2', 3, 'Computación y Sistemas Inteligentes', 4);


INSERT INTO estudiante_curso (estudiante_id, curso_id) VALUES (1,1);
INSERT INTO estudiante_curso (estudiante_id, curso_id) VALUES (1,2);
INSERT INTO estudiante_curso (estudiante_id, curso_id) VALUES (1,3);
INSERT INTO estudiante_curso (estudiante_id, curso_id) VALUES (2,1);
INSERT INTO estudiante_curso (estudiante_id, curso_id) VALUES (2,2);
INSERT INTO estudiante_curso (estudiante_id, curso_id) VALUES (2,3);
INSERT INTO estudiante_curso (estudiante_id, curso_id) VALUES (3,1);
INSERT INTO estudiante_curso (estudiante_id, curso_id) VALUES (3,2);
INSERT INTO estudiante_curso (estudiante_id, curso_id) VALUES (3,3);

INSERT INTO estudiante_curso_Many (estudiante_id, curso_id) VALUES (1,1);
INSERT INTO estudiante_curso_Many (estudiante_id, curso_id) VALUES (1,2);
INSERT INTO estudiante_curso_Many (estudiante_id, curso_id) VALUES (1,3);
INSERT INTO estudiante_curso_Many (estudiante_id, curso_id) VALUES (2,1);
INSERT INTO estudiante_curso_Many (estudiante_id, curso_id) VALUES (2,2);
INSERT INTO estudiante_curso_Many (estudiante_id, curso_id) VALUES (2,3);
INSERT INTO estudiante_curso_Many (estudiante_id, curso_id) VALUES (3,1);
INSERT INTO estudiante_curso_Many (estudiante_id, curso_id) VALUES (3,2);
INSERT INTO estudiante_curso_Many (estudiante_id, curso_id) VALUES (3,3);