package com.example.proyecto1bases.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "INSCRIPCION")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inscripcion")
    private Long idInscripcion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_quiniela", nullable = false)
    private Quiniela quiniela;

    @Column(name = "fecha_inscripcion")
    private LocalDateTime fechaInscripcion;

    @Column(name = "acepto_reglas")
    private Boolean aceptoReglas;

    public Inscripcion() {}

    // ── Getters / Setters ─────────────────────────────────────────

    public Long getIdInscripcion() { return idInscripcion; }
    public void setIdInscripcion(Long idInscripcion) { this.idInscripcion = idInscripcion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Quiniela getQuiniela() { return quiniela; }
    public void setQuiniela(Quiniela quiniela) { this.quiniela = quiniela; }

    public LocalDateTime getFechaInscripcion() { return fechaInscripcion; }
    public void setFechaInscripcion(LocalDateTime fechaInscripcion) { this.fechaInscripcion = fechaInscripcion; }

    public Boolean getAceptoReglas() { return aceptoReglas; }
    public void setAceptoReglas(Boolean aceptoReglas) { this.aceptoReglas = aceptoReglas; }
}
