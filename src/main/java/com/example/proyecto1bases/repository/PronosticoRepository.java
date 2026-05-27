package com.example.proyecto1bases.repository;

import com.example.proyecto1bases.model.Partido;
import com.example.proyecto1bases.model.Pronostico;
import com.example.proyecto1bases.model.Quiniela;
import com.example.proyecto1bases.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PronosticoRepository extends JpaRepository<Pronostico, Long> {
    List<Pronostico> findByUsuario(Usuario usuario);
    List<Pronostico> findByUsuarioAndQuiniela(Usuario usuario, Quiniela quiniela);
    Optional<Pronostico> findByUsuarioAndPartidoAndQuiniela(Usuario usuario, Partido partido, Quiniela quiniela);
}
