package com.example.proyecto1bases.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class QuinielaPartidoId implements Serializable {

    @Column(name = "id_quiniela")
    private Long idQuiniela;

    @Column(name = "id_partido")
    private Long idPartido;

    public QuinielaPartidoId() {}

    public QuinielaPartidoId(Long idQuiniela, Long idPartido) {
        this.idQuiniela = idQuiniela;
        this.idPartido = idPartido;
    }

    public Long getIdQuiniela() { return idQuiniela; }
    public void setIdQuiniela(Long idQuiniela) { this.idQuiniela = idQuiniela; }

    public Long getIdPartido() { return idPartido; }
    public void setIdPartido(Long idPartido) { this.idPartido = idPartido; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuinielaPartidoId)) return false;
        QuinielaPartidoId that = (QuinielaPartidoId) o;
        return Objects.equals(idQuiniela, that.idQuiniela)
            && Objects.equals(idPartido, that.idPartido);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idQuiniela, idPartido);
    }
}
