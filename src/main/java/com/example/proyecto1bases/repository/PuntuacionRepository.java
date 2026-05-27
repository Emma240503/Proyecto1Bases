package com.example.proyecto1bases.repository;

import com.example.proyecto1bases.model.Puntuacion;
import com.example.proyecto1bases.model.Quiniela;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PuntuacionRepository extends JpaRepository<Puntuacion, Long> {
    List<Puntuacion> findByQuinielaOrderByPuntosTotalesDesc(Quiniela quiniela);
}
