package com.example.proyecto1bases.repository;

import com.example.proyecto1bases.model.Quiniela;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuinielaRepository extends JpaRepository<Quiniela, Long> {
    List<Quiniela> findByEstado(String estado);
}
