package com.example.proyecto1bases.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "QUINIELA")
public class Quiniela {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_quiniela")
    private Long idQuiniela;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "reglas")
    private String reglas;

    @Column(name = "fecha_inicio_inscripcion")
    private LocalDate fechaInicioInscripcion;

    @Column(name = "fecha_cierre_inscripcion")
    private LocalDate fechaCierreInscripcion;

    @Column(name = "estado", nullable = false)
    private String estado; // ABIERTA | CERRADA | FINALIZADA

    @Column(name = "modalidad")
    private String modalidad;

    @Column(name = "tipo_puntuacion")
    private String tipoPuntuacion;

    public Quiniela() {}

    // ── Getters / Setters ─────────────────────────────────────────

    public Long getIdQuiniela() { return idQuiniela; }
    public void setIdQuiniela(Long idQuiniela) { this.idQuiniela = idQuiniela; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getReglas() { return reglas; }
    public void setReglas(String reglas) { this.reglas = reglas; }

    public LocalDate getFechaInicioInscripcion() { return fechaInicioInscripcion; }
    public void setFechaInicioInscripcion(LocalDate f) { this.fechaInicioInscripcion = f; }

    public LocalDate getFechaCierreInscripcion() { return fechaCierreInscripcion; }
    public void setFechaCierreInscripcion(LocalDate f) { this.fechaCierreInscripcion = f; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public String getTipoPuntuacion() { return tipoPuntuacion; }
    public void setTipoPuntuacion(String tipoPuntuacion) { this.tipoPuntuacion = tipoPuntuacion; }
}
