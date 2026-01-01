DROP DATABASE IF EXISTS Libreria;
CREATE DATABASE Libreria;
USE Libreria;


CREATE TABLE usuarios (
      idUsuario int PRIMARY KEY AUTO_INCREMENT,
      nombre VARCHAR(50) NOT NULL UNIQUE,
      password VARCHAR(50) NOT NULL,
      permisos VARCHAR(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO usuarios (nombre, password, permisos) VALUES
     ('admin', '1234', 'ADMIN'),
     ('pepe', '1234', 'USER');



CREATE TABLE editoriales (
    ideditorial int PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    ciudad VARCHAR(50) DEFAULT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO editoriales (nombre, ciudad) VALUES
    ('Planeta', 'Madrid'),
    ('Santillana', 'Barcelona'),
    ('OReilly', 'California');


CREATE TABLE  libros (
     idLibro int PRIMARY KEY AUTO_INCREMENT,
     titulo VARCHAR(50) NOT NULL,
     isbn VARCHAR(20) UNIQUE,
     ideditorial int DEFAULT NULL,
     FOREIGN KEY (ideditorial) REFERENCES editoriales(ideditorial) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO libros (titulo, isbn, ideditorial) VALUES
    ('Don Quijote', '978-123456', 1),
    ('Cien Años de Soledad', '978-654321', 1),
    ('Learning Java', '978-999999', 3);


CREATE TABLE  autores (
    idAutor int PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    nacionalidad VARCHAR(50) DEFAULT NULL
    ) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;

INSERT INTO autores (nombre, nacionalidad)VALUES
    ('Miguel de Cervantes', 'Española'),
    ('Gabriel García Márquez', 'Colombiana'),
    ('Kath Simens', 'Americana');


CREATE TABLE libro_autor (
    idLibro int NOT NULL,
    idAutor int NOT NULL,
    PRIMARY KEY (idLibro, idAutor),
    CONSTRAINT fk_libro FOREIGN KEY (idLibro) REFERENCES libros(idLibro) ON UPDATE CASCADE,
    CONSTRAINT fk_autor FOREIGN KEY (idAutor) REFERENCES autores(idAutor) ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO libro_autor (idLibro, idAutor) VALUES
(1, 1),
(2, 2);