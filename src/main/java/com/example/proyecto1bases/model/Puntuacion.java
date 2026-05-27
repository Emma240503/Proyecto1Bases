package com.example.proyecto1bases.model;

import jakarta.persistence.*;

@Entity
@Table(name = "PUNTUACION")
public class Puntuacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_puntuacion")
    private Long idPuntuacion;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_quiniela", nullable = false)
    private Quiniela quiniela;

    @Column(name = "puntos_totales")
    private Integer puntosTotales;

    public Puntuacion() {}

    // ── Getters / Setters ─────────────────────────────────────────

    public Long getIdPuntuacion() { return idPuntuacion; }
    public void setIdPuntuacion(Long idPuntuacion) { this.idPuntuacion = idPuntuacion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Quiniela getQuiniela() { return quiniela; }
    public void setQuiniela(Quiniela quiniela) { this.quiniela = quiniela; }

    public Integer getPuntosTotales() { return puntosTotales; }
    public void setPuntosTotales(Integer puntosTotales) { this.puntosTotales = puntosTotales; }
}
