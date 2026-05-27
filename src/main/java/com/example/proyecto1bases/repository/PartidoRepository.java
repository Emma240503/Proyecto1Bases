package com.example.proyecto1bases.repository;

import com.example.proyecto1bases.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartidoRepository extends JpaRepository<Partido, Long> {
}
