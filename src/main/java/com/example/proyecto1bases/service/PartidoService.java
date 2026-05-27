package com.example.proyecto1bases.service;

import com.example.proyecto1bases.model.Partido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Servicio de Partidos.
 * Todas las operaciones usan SimpleJdbcCall sobre los procedimientos almacenados.
 *
 * NOTA: Se asume que el SP InsertarPartido acepta el parámetro @id_quiniela
 * para insertar simultáneamente en QUINIELA_PARTIDO.
 * Si tu SP no lo acepta, elimina ese parámetro del método insertarPartido.
 */
@Service
public class PartidoService {

    @Autowired
    private DataSource dataSource;

    /**
     * Crea un partido y lo asocia a una quiniela (SP InsertarPartido).
     *
     * @param partido   datos del partido
     * @param idQuiniela quiniela a la que pertenece (se pasa al SP)
     */
    public void insertarPartido(Partido partido, Long idQuiniela) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("InsertarPartido");

        String estado = (partido.getEstado() != null && !partido.getEstado().isBlank())
                ? partido.getEstado() : "PENDIENTE";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("equipo_local",     partido.getEquipoLocal())
                .addValue("equipo_visitante", partido.getEquipoVisitante())
                .addValue("fecha_hora",       partido.getFechaHora())
                .addValue("estado",           estado)
                .addValue("id_quiniela",      idQuiniela); // si el SP no lo usa, eliminar esta línea

        call.execute(params);
    }

    /** Retorna los partidos de una quiniela (SP ObtenerPartidosPorQuiniela). */
    public List<Partido> obtenerPorQuiniela(Long idQuiniela) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ObtenerPartidosPorQuiniela")
                .returningResultSet("partidos", new PartidoRowMapper());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id_quiniela", idQuiniela);

        Map<String, Object> result = call.execute(params);

        @SuppressWarnings("unchecked")
        List<Partido> lista = (List<Partido>) result.getOrDefault("partidos", Collections.emptyList());
        return lista;
    }

    /** Retorna un partido por id (SP ObtenerPartidoPorId). */
    public Partido obtenerPorId(Long idPartido) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ObtenerPartidoPorId")
                .returningResultSet("partido", new PartidoRowMapper());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id_partido", idPartido);

        Map<String, Object> result = call.execute(params);

        @SuppressWarnings("unchecked")
        List<Partido> lista = (List<Partido>) result.get("partido");
        return (lista != null && !lista.isEmpty()) ? lista.get(0) : null;
    }

    /**
     * Actualiza el resultado de un partido (SP ActualizarResultadoPartido).
     * Este SP también llama internamente a CalcularPuntuacion.
     */
    public void actualizarResultado(Long idPartido, Integer golesLocal, Integer golesVisitante) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ActualizarResultadoPartido");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id_partido",      idPartido)
                .addValue("goles_local",     golesLocal)
                .addValue("goles_visitante", golesVisitante);

        call.execute(params);
    }

    /** Elimina un partido (SP EliminarPartido). */
    public void eliminarPartido(Long idPartido) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("EliminarPartido");

        call.execute(new MapSqlParameterSource().addValue("id_partido", idPartido));
    }

    // ── RowMapper interno ─────────────────────────────────────────

    static class PartidoRowMapper implements RowMapper<Partido> {
        @Override
        public Partido mapRow(ResultSet rs, int rowNum) throws SQLException {
            Partido p = new Partido();
            p.setIdPartido(rs.getLong("id_partido"));
            p.setEquipoLocal(rs.getString("equipo_local"));
            p.setEquipoVisitante(rs.getString("equipo_visitante"));

            Timestamp ts = rs.getTimestamp("fecha_hora");
            if (ts != null) p.setFechaHora(ts.toLocalDateTime());

            p.setEstado(rs.getString("estado"));

            Object gl = rs.getObject("goles_local");
            if (gl != null) p.setGolesLocal(rs.getInt("goles_local"));

            Object gv = rs.getObject("goles_visitante");
            if (gv != null) p.setGolesVisitante(rs.getInt("goles_visitante"));

            return p;
        }
    }
}
