DROP DATABASE IF EXISTS Juego;
CREATE DATABASE Juego;
USE Juego;

-- 玩家表
CREATE TABLE Jugador (
    idJugador INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL,
    password VARCHAR(64) NOT NULL,
    telefono INT(9)
);

INSERT INTO Jugador (idJugador, nombre, password, telefono) VALUES
(1, "David", SHA2("david", 256), 611222333),
(2, "Angel", SHA2("angel", 256), 611512183),
(3, "Lucia", SHA2("lucia", 256), 611224013),
(4, "Martina", SHA2("martina", 256), 618434555);


CREATE TABLE Amigos (
    idJugador1 INT NOT NULL,
    idJugador2 INT NOT NULL,
    estado VARCHAR(20) DEFAULT 'pendiente', -- pendiente, aceptado, bloqueado
    CHECK (idJugador1 < idJugador2),
    PRIMARY KEY (idJugador1, idJugador2),
    FOREIGN KEY (idJugador1) REFERENCES Jugador(idJugador),
    FOREIGN KEY (idJugador2) REFERENCES Jugador(idJugador)
);


INSERT INTO Amigos (idJugador1, idJugador2, estado) VALUES
            (LEAST(1, 2), GREATEST(1, 2), 'pendiente'),
            (LEAST(1, 3), GREATEST(1, 3), 'pendiente'),
            (LEAST(2, 4), GREATEST(2, 4), 'aceptado');

CREATE TABLE Puntos (
    idPunto INT AUTO_INCREMENT PRIMARY KEY,
    idJugador INT NOT NULL,
    puntos INT DEFAULT 0,
    FOREIGN KEY (idJugador) REFERENCES Jugador(idJugador)
);


INSERT INTO Puntos (idJugador, puntos) VALUES
(1, 10),
(2, 5),
(3, 7),
(4, 0);
