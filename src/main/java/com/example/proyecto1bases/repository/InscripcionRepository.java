package com.example.proyecto1bases.repository;

import com.example.proyecto1bases.model.Inscripcion;
import com.example.proyecto1bases.model.Quiniela;
import com.example.proyecto1bases.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    List<Inscripcion> findByQuiniela(Quiniela quiniela);
    Optional<Inscripcion> findByUsuarioAndQuiniela(Usuario usuario, Quiniela quiniela);
    boolean existsByUsuarioAndQuiniela(Usuario usuario, Quiniela quiniela);
}
