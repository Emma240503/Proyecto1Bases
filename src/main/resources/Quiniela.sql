-- Sistema de Quinielas de Fútbol
-- EIF211 - Diseño e Implementación de Bases de Datos

CREATE DATABASE QuinielaFutbol;
GO

USE QuinielaFutbol;
GO



CREATE TABLE USUARIO (
    id_usuario        INT           IDENTITY(1,1) PRIMARY KEY,
    nombre_completo   VARCHAR(100)  NOT NULL,
    username          VARCHAR(50)   NOT NULL UNIQUE,
    email             VARCHAR(100)  NOT NULL UNIQUE,
    contrasena        VARCHAR(255)  NOT NULL,
    fecha_nacimiento  DATE          NOT NULL,
    tipo_usuario      VARCHAR(20)   NOT NULL DEFAULT 'JUGADOR'
);
GO

CREATE TABLE QUINIELA (
    id_quiniela               INT           IDENTITY(1,1) PRIMARY KEY,
    nombre                    VARCHAR(100)  NOT NULL UNIQUE,
    descripcion               VARCHAR(500)  NULL,
    reglas                    VARCHAR(1000) NULL,
    fecha_inicio_inscripcion  DATE          NOT NULL,
    fecha_cierre_inscripcion  DATE          NOT NULL,
    estado                    VARCHAR(20)   NOT NULL DEFAULT 'ABIERTA',
    modalidad                 VARCHAR(20)   NOT NULL DEFAULT 'PUBLICA',
    tipo_puntuacion           VARCHAR(50)   NOT NULL DEFAULT 'BASICO'
);
GO

CREATE TABLE PARTIDO (
    id_partido        INT          IDENTITY(1,1) PRIMARY KEY,
    equipo_local      VARCHAR(100) NOT NULL,
    equipo_visitante  VARCHAR(100) NOT NULL,
    fecha_hora        DATETIME     NOT NULL,
    estado            VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    goles_local       INT          NULL,
    goles_visitante   INT          NULL
);
GO

CREATE TABLE INSCRIPCION (
    id_inscripcion    INT  IDENTITY(1,1) PRIMARY KEY,
    id_usuario        INT  NOT NULL,
    id_quiniela       INT  NOT NULL,
    fecha_inscripcion DATE NOT NULL DEFAULT CAST(GETDATE() AS DATE),
    acepto_reglas     BIT  NOT NULL DEFAULT 0,
    CONSTRAINT FK_INSCRIPCION_USUARIO  FOREIGN KEY (id_usuario)  REFERENCES USUARIO(id_usuario),
    CONSTRAINT FK_INSCRIPCION_QUINIELA FOREIGN KEY (id_quiniela) REFERENCES QUINIELA(id_quiniela),
    CONSTRAINT UQ_INSCRIPCION UNIQUE (id_usuario, id_quiniela)
);
GO

CREATE TABLE QUINIELA_PARTIDO (
    id_quiniela INT NOT NULL,
    id_partido  INT NOT NULL,
    CONSTRAINT PK_QUINIELA_PARTIDO PRIMARY KEY (id_quiniela, id_partido),
    CONSTRAINT FK_QP_QUINIELA FOREIGN KEY (id_quiniela) REFERENCES QUINIELA(id_quiniela),
    CONSTRAINT FK_QP_PARTIDO  FOREIGN KEY (id_partido)  REFERENCES PARTIDO(id_partido)
);
GO

CREATE TABLE PRONOSTICO (
    id_pronostico            INT      IDENTITY(1,1) PRIMARY KEY,
    id_usuario               INT      NOT NULL,
    id_partido               INT      NOT NULL,
    id_quiniela              INT      NOT NULL,
    goles_local_predicho     INT      NOT NULL,
    goles_visitante_predicho INT      NOT NULL,
    fecha_hora_ingreso       DATETIME NOT NULL DEFAULT GETDATE(),
    CONSTRAINT FK_PRON_USUARIO  FOREIGN KEY (id_usuario)  REFERENCES USUARIO(id_usuario),
    CONSTRAINT FK_PRON_PARTIDO  FOREIGN KEY (id_partido)  REFERENCES PARTIDO(id_partido),
    CONSTRAINT FK_PRON_QUINIELA FOREIGN KEY (id_quiniela) REFERENCES QUINIELA(id_quiniela),
    CONSTRAINT UQ_PRONOSTICO UNIQUE (id_usuario, id_partido, id_quiniela)
);
GO

CREATE TABLE PUNTUACION (
    id_puntuacion  INT IDENTITY(1,1) PRIMARY KEY,
    id_usuario     INT NOT NULL,
    id_quiniela    INT NOT NULL,
    puntos_totales INT NOT NULL DEFAULT 0,
    CONSTRAINT FK_PUN_USUARIO  FOREIGN KEY (id_usuario)  REFERENCES USUARIO(id_usuario),
    CONSTRAINT FK_PUN_QUINIELA FOREIGN KEY (id_quiniela) REFERENCES QUINIELA(id_quiniela),
    CONSTRAINT UQ_PUNTUACION UNIQUE (id_usuario, id_quiniela)
);
GO


-- PROCEDIMIENTOS - USUARIO


CREATE PROCEDURE InsertarUsuario
    @nombre_completo  VARCHAR(100),
    @username         VARCHAR(50),
    @email            VARCHAR(100),
    @contrasena       VARCHAR(255),
    @fecha_nacimiento DATE,
    @tipo_usuario     VARCHAR(20)
AS
BEGIN
    INSERT INTO USUARIO (nombre_completo, username, email, contrasena, fecha_nacimiento, tipo_usuario)
    VALUES (@nombre_completo, @username, @email, @contrasena, @fecha_nacimiento, @tipo_usuario);
END;
GO

CREATE PROCEDURE ObtenerUsuarios
AS
BEGIN
    SELECT id_usuario, nombre_completo, username, email, fecha_nacimiento, tipo_usuario
    FROM USUARIO;
END;
GO

CREATE PROCEDURE ObtenerUsuarioPorUsername
    @username VARCHAR(50)
AS
BEGIN
    SELECT id_usuario, nombre_completo, username, email, contrasena, fecha_nacimiento, tipo_usuario
    FROM USUARIO
    WHERE username = @username;
END;
GO

CREATE PROCEDURE ActualizarUsuario
    @id_usuario       INT,
    @nombre_completo  VARCHAR(100),
    @email            VARCHAR(100),
    @fecha_nacimiento DATE
AS
BEGIN
    UPDATE USUARIO
    SET nombre_completo  = @nombre_completo,
        email            = @email,
        fecha_nacimiento = @fecha_nacimiento
    WHERE id_usuario = @id_usuario;
END;
GO

CREATE PROCEDURE EliminarUsuario
    @id_usuario INT
AS
BEGIN
    DELETE FROM PUNTUACION  WHERE id_usuario = @id_usuario;
    DELETE FROM PRONOSTICO  WHERE id_usuario = @id_usuario;
    DELETE FROM INSCRIPCION WHERE id_usuario = @id_usuario;
    DELETE FROM USUARIO     WHERE id_usuario = @id_usuario;
END;
GO


-- PROCEDIMIENTOS - QUINIELA

CREATE PROCEDURE InsertarQuiniela
    @nombre                   VARCHAR(100),
    @descripcion              VARCHAR(500),
    @reglas                   VARCHAR(1000),
    @fecha_inicio_inscripcion DATE,
    @fecha_cierre_inscripcion DATE,
    @modalidad                VARCHAR(20),
    @tipo_puntuacion          VARCHAR(50)
AS
BEGIN
    INSERT INTO QUINIELA (nombre, descripcion, reglas, fecha_inicio_inscripcion, fecha_cierre_inscripcion, estado, modalidad, tipo_puntuacion)
    VALUES (@nombre, @descripcion, @reglas, @fecha_inicio_inscripcion, @fecha_cierre_inscripcion, 'ABIERTA', @modalidad, @tipo_puntuacion);
END;
GO

CREATE PROCEDURE ObtenerQuinielas
AS
BEGIN
    SELECT id_quiniela, nombre, descripcion, reglas,
           fecha_inicio_inscripcion, fecha_cierre_inscripcion,
           estado, modalidad, tipo_puntuacion
    FROM QUINIELA;
END;
GO

CREATE PROCEDURE ObtenerQuinielaPorId
    @id_quiniela INT
AS
BEGIN
    SELECT id_quiniela, nombre, descripcion, reglas,
           fecha_inicio_inscripcion, fecha_cierre_inscripcion,
           estado, modalidad, tipo_puntuacion
    FROM QUINIELA
    WHERE id_quiniela = @id_quiniela;
END;
GO

CREATE PROCEDURE ActualizarQuiniela
    @id_quiniela              INT,
    @nombre                   VARCHAR(100),
    @descripcion              VARCHAR(500),
    @reglas                   VARCHAR(1000),
    @fecha_inicio_inscripcion DATE,
    @fecha_cierre_inscripcion DATE,
    @modalidad                VARCHAR(20)
AS
BEGIN
    UPDATE QUINIELA
    SET nombre                   = @nombre,
        descripcion              = @descripcion,
        reglas                   = @reglas,
        fecha_inicio_inscripcion = @fecha_inicio_inscripcion,
        fecha_cierre_inscripcion = @fecha_cierre_inscripcion,
        modalidad                = @modalidad
    WHERE id_quiniela = @id_quiniela;
END;
GO

CREATE PROCEDURE CambiarEstadoQuiniela
    @id_quiniela INT,
    @estado      VARCHAR(20)
AS
BEGIN
    UPDATE QUINIELA SET estado = @estado WHERE id_quiniela = @id_quiniela;
END;
GO

CREATE PROCEDURE EliminarQuiniela
    @id_quiniela INT
AS
BEGIN
    DELETE FROM PUNTUACION       WHERE id_quiniela = @id_quiniela;
    DELETE FROM PRONOSTICO       WHERE id_quiniela = @id_quiniela;
    DELETE FROM INSCRIPCION      WHERE id_quiniela = @id_quiniela;
    DELETE FROM QUINIELA_PARTIDO WHERE id_quiniela = @id_quiniela;
    DELETE FROM QUINIELA         WHERE id_quiniela = @id_quiniela;
END;
GO


-- PROCEDIMIENTOS - PARTIDO

CREATE PROCEDURE InsertarPartido
    @equipo_local     VARCHAR(100),
    @equipo_visitante VARCHAR(100),
    @fecha_hora       DATETIME,
    @id_quiniela      INT
AS
BEGIN
    DECLARE @id_partido INT;
    INSERT INTO PARTIDO (equipo_local, equipo_visitante, fecha_hora, estado)
    VALUES (@equipo_local, @equipo_visitante, @fecha_hora, 'PENDIENTE');
    SET @id_partido = SCOPE_IDENTITY();
    INSERT INTO QUINIELA_PARTIDO (id_quiniela, id_partido)
    VALUES (@id_quiniela, @id_partido);
END;
GO

CREATE PROCEDURE ObtenerPartidosPorQuiniela
    @id_quiniela INT
AS
BEGIN
    SELECT P.id_partido, P.equipo_local, P.equipo_visitante,
           P.fecha_hora, P.estado, P.goles_local, P.goles_visitante
    FROM PARTIDO P
    INNER JOIN QUINIELA_PARTIDO QP ON QP.id_partido = P.id_partido
    WHERE QP.id_quiniela = @id_quiniela;
END;
GO

CREATE PROCEDURE ObtenerPartidoPorId
    @id_partido INT
AS
BEGIN
    SELECT id_partido, equipo_local, equipo_visitante,
           fecha_hora, estado, goles_local, goles_visitante
    FROM PARTIDO
    WHERE id_partido = @id_partido;
END;
GO

CREATE PROCEDURE ActualizarResultadoPartido
    @id_partido      INT,
    @goles_local     INT,
    @goles_visitante INT
AS
BEGIN
    UPDATE PARTIDO
    SET goles_local     = @goles_local,
        goles_visitante = @goles_visitante,
        estado          = 'FINALIZADO'
    WHERE id_partido = @id_partido;
    EXEC CalcularPuntuacion @id_partido;
END;
GO

CREATE PROCEDURE EliminarPartido
    @id_partido INT
AS
BEGIN
    DELETE FROM PRONOSTICO       WHERE id_partido = @id_partido;
    DELETE FROM QUINIELA_PARTIDO WHERE id_partido = @id_partido;
    DELETE FROM PARTIDO          WHERE id_partido = @id_partido;
END;
GO


-- PROCEDIMIENTOS - INSCRIPCION

CREATE PROCEDURE InscribirUsuario
    @id_usuario  INT,
    @id_quiniela INT
AS
BEGIN
    INSERT INTO INSCRIPCION (id_usuario, id_quiniela, acepto_reglas)
    VALUES (@id_usuario, @id_quiniela, 1);
    INSERT INTO PUNTUACION (id_usuario, id_quiniela, puntos_totales)
    VALUES (@id_usuario, @id_quiniela, 0);
END;
GO

CREATE PROCEDURE ObtenerInscripcionesPorQuiniela
    @id_quiniela INT
AS
BEGIN
    SELECT U.id_usuario, U.nombre_completo, U.username, I.fecha_inscripcion
    FROM INSCRIPCION I
    INNER JOIN USUARIO U ON U.id_usuario = I.id_usuario
    WHERE I.id_quiniela = @id_quiniela;
END;
GO


-- PROCEDIMIENTOS - PRONOSTICO

CREATE PROCEDURE InsertarPronostico
    @id_usuario               INT,
    @id_partido               INT,
    @id_quiniela              INT,
    @goles_local_predicho     INT,
    @goles_visitante_predicho INT
AS
BEGIN
    INSERT INTO PRONOSTICO (id_usuario, id_partido, id_quiniela, goles_local_predicho, goles_visitante_predicho)
    VALUES (@id_usuario, @id_partido, @id_quiniela, @goles_local_predicho, @goles_visitante_predicho);
END;
GO

CREATE PROCEDURE ObtenerPronosticosPorUsuario
    @id_usuario  INT,
    @id_quiniela INT
AS
BEGIN
    SELECT PR.id_pronostico,
           P.equipo_local, P.equipo_visitante, P.fecha_hora,
           P.estado, P.goles_local, P.goles_visitante,
           PR.goles_local_predicho, PR.goles_visitante_predicho,
           PR.fecha_hora_ingreso
    FROM PRONOSTICO PR
    INNER JOIN PARTIDO P ON P.id_partido = PR.id_partido
    WHERE PR.id_usuario = @id_usuario AND PR.id_quiniela = @id_quiniela;
END;
GO


-- PROCEDIMIENTOS - PUNTUACION

CREATE PROCEDURE CalcularPuntuacion
    @id_partido INT
AS
BEGIN
    DECLARE @goles_local     INT;
    DECLARE @goles_visitante INT;
    DECLARE @resultado_real  VARCHAR(10);

    SELECT @goles_local = goles_local, @goles_visitante = goles_visitante
    FROM PARTIDO
    WHERE id_partido = @id_partido;

    SET @resultado_real = CASE
        WHEN @goles_local > @goles_visitante THEN 'LOCAL'
        WHEN @goles_local < @goles_visitante THEN 'VISITANTE'
        ELSE 'EMPATE'
    END;

    UPDATE PUN
    SET PUN.puntos_totales = PUN.puntos_totales +
        CASE
            WHEN PR.goles_local_predicho    = @goles_local
             AND PR.goles_visitante_predicho = @goles_visitante THEN 3
            WHEN (
                (PR.goles_local_predicho > PR.goles_visitante_predicho AND @resultado_real = 'LOCAL') OR
                (PR.goles_local_predicho < PR.goles_visitante_predicho AND @resultado_real = 'VISITANTE') OR
                (PR.goles_local_predicho = PR.goles_visitante_predicho AND @resultado_real = 'EMPATE')
            ) THEN 1
            ELSE 0
        END
    FROM PUNTUACION PUN
    INNER JOIN PRONOSTICO PR
        ON PR.id_usuario  = PUN.id_usuario
       AND PR.id_quiniela = PUN.id_quiniela
       AND PR.id_partido  = @id_partido;
END;
GO

CREATE PROCEDURE ObtenerRanking
    @id_quiniela INT
AS
BEGIN
    SELECT
        RANK() OVER (ORDER BY PUN.puntos_totales DESC) AS posicion,
        U.username,
        U.nombre_completo,
        PUN.puntos_totales
    FROM PUNTUACION PUN
    INNER JOIN USUARIO U ON U.id_usuario = PUN.id_usuario
    WHERE PUN.id_quiniela = @id_quiniela
    ORDER BY posicion;
END;
GO


-- 
-- VISTA: Ranking global
-- 
CREATE VIEW VW_RANKING AS
    SELECT
        Q.nombre AS quiniela,
        U.username,
        U.nombre_completo,
        P.puntos_totales,
        RANK() OVER (
            PARTITION BY P.id_quiniela
            ORDER BY P.puntos_totales DESC
        ) AS posicion
    FROM PUNTUACION P
    INNER JOIN USUARIO  U ON U.id_usuario  = P.id_usuario
    INNER JOIN QUINIELA Q ON Q.id_quiniela = P.id_quiniela;
GO


-- Credenciales admin: usuario=admin, contrasena=1234

INSERT INTO USUARIO (nombre_completo, username, email, contrasena, fecha_nacimiento, tipo_usuario)
VALUES
    ('Admin Sistema',      'admin',   'admin@quiniela.com',      '$2a$10$5QLXSCg0FupyAPrdu6nLjeBOiCEHsdRowuqWgHd5zmi4UGdbS.SQC', '1990-01-01', 'ADMINISTRADOR'),
    ('Emmanuel Rodriguez', 'emma',    'emmaropi240503@gmail.com', '$2a$10$5QLXSCg0FupyAPrdu6nLjeBOiCEHsdRowuqWgHd5zmi4UGdbS.SQC', '2003-05-24', 'JUGADOR'),
    ('Luisa Perez',        'lperez',  'lperez@email.com',         '$2a$10$5QLXSCg0FupyAPrdu6nLjeBOiCEHsdRowuqWgHd5zmi4UGdbS.SQC', '1998-09-22', 'JUGADOR'),
    ('Marco Solano',       'msolano', 'msolano@email.com',        '$2a$10$5QLXSCg0FupyAPrdu6nLjeBOiCEHsdRowuqWgHd5zmi4UGdbS.SQC', '1993-03-10', 'JUGADOR');
GO

INSERT INTO QUINIELA (nombre, descripcion, reglas, fecha_inicio_inscripcion, fecha_cierre_inscripcion, estado, modalidad, tipo_puntuacion)
VALUES ('Copa America 2026', 'Quiniela de la Copa America', 'Marcador exacto: 3 pts. Resultado correcto: 1 pt.', '2026-05-01', '2026-06-01', 'ABIERTA', 'PUBLICA', 'BASICO');
GO

INSERT INTO PARTIDO (equipo_local, equipo_visitante, fecha_hora, estado)
VALUES
    ('Costa Rica', 'Mexico',    '2026-06-10 18:00:00', 'PENDIENTE'),
    ('Brasil',     'Argentina', '2026-06-11 20:00:00', 'PENDIENTE');
GO

INSERT INTO QUINIELA_PARTIDO (id_quiniela, id_partido) VALUES (1, 1), (1, 2);
GO

EXEC InscribirUsuario @id_usuario = 2, @id_quiniela = 1;
EXEC InscribirUsuario @id_usuario = 3, @id_quiniela = 1;
EXEC InscribirUsuario @id_usuario = 4, @id_quiniela = 1;
GO


USE QuinielaFutbol;
EXEC ObtenerRanking @id_quiniela = 1;