package com.example.proyecto1bases.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "PRONOSTICO")
public class Pronostico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pronostico")
    private Long idPronostico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_partido", nullable = false)
    private Partido partido;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_quiniela", nullable = false)
    private Quiniela quiniela;

    @Column(name = "goles_local_predicho")
    private Integer golesLocalPredicho;

    @Column(name = "goles_visitante_predicho")
    private Integer golesVisitantePredicho;

    @Column(name = "fecha_hora_ingreso")
    private LocalDateTime fechaHoraIngreso;

    public Pronostico() {}

    // ── Getters / Setters ─────────────────────────────────────────

    public Long getIdPronostico() { return idPronostico; }
    public void setIdPronostico(Long idPronostico) { this.idPronostico = idPronostico; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }

    public Quiniela getQuiniela() { return quiniela; }
    public void setQuiniela(Quiniela quiniela) { this.quiniela = quiniela; }

    public Integer getGolesLocalPredicho() { return golesLocalPredicho; }
    public void setGolesLocalPredicho(Integer golesLocalPredicho) { this.golesLocalPredicho = golesLocalPredicho; }

    public Integer getGolesVisitantePredicho() { return golesVisitantePredicho; }
    public void setGolesVisitantePredicho(Integer goles) { this.golesVisitantePredicho = goles; }

    public LocalDateTime getFechaHoraIngreso() { return fechaHoraIngreso; }
    public void setFechaHoraIngreso(LocalDateTime f) { this.fechaHoraIngreso = f; }
}
