package com.example.proyecto1bases.model;

import jakarta.persistence.*;

@Entity
@Table(name = "QUINIELA_PARTIDO")
public class QuinielaPartido {

    @EmbeddedId
    private QuinielaPartidoId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idQuiniela")
    @JoinColumn(name = "id_quiniela")
    private Quiniela quiniela;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("idPartido")
    @JoinColumn(name = "id_partido")
    private Partido partido;

    public QuinielaPartido() {}

    public QuinielaPartido(Quiniela quiniela, Partido partido) {
        this.quiniela = quiniela;
        this.partido = partido;
        this.id = new QuinielaPartidoId(quiniela.getIdQuiniela(), partido.getIdPartido());
    }

    public QuinielaPartidoId getId() { return id; }
    public void setId(QuinielaPartidoId id) { this.id = id; }

    public Quiniela getQuiniela() { return quiniela; }
    public void setQuiniela(Quiniela quiniela) { this.quiniela = quiniela; }

    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }
}
