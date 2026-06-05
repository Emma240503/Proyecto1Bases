package com.example.proyecto1bases.service;

import com.example.proyecto1bases.model.Quiniela;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servicio de Quinielas e Inscripciones.
 * Todas las operaciones usan SimpleJdbcCall sobre los procedimientos almacenados.
 */
@Service
public class QuinielaService {

    @Autowired
    private DataSource dataSource;


    public void insertarQuiniela(Quiniela q) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("InsertarQuiniela");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nombre",                  q.getNombre())
                .addValue("descripcion",             q.getDescripcion())
                .addValue("reglas",                  q.getReglas())
                .addValue("fecha_inicio_inscripcion", q.getFechaInicioInscripcion())
                .addValue("fecha_cierre_inscripcion", q.getFechaCierreInscripcion())
                .addValue("estado",                  q.getEstado())
                .addValue("modalidad",               q.getModalidad())
                .addValue("tipo_puntuacion",         q.getTipoPuntuacion());

        call.execute(params);
    }

    public List<Quiniela> obtenerTodas() {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ObtenerQuinielas")
                .returningResultSet("quinielas", new QuinielaRowMapper());

        Map<String, Object> result = call.execute();

        @SuppressWarnings("unchecked")
        List<Quiniela> lista = (List<Quiniela>) result.getOrDefault("quinielas", Collections.emptyList());
        return lista;
    }

    public Quiniela obtenerPorId(Long idQuiniela) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ObtenerQuinielaPorId")
                .returningResultSet("quiniela", new QuinielaRowMapper());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id_quiniela", idQuiniela);

        Map<String, Object> result = call.execute(params);

        @SuppressWarnings("unchecked")
        List<Quiniela> lista = (List<Quiniela>) result.get("quiniela");
        return (lista != null && !lista.isEmpty()) ? lista.get(0) : null;
    }

    public void actualizarQuiniela(Quiniela q) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ActualizarQuiniela");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id_quiniela",             q.getIdQuiniela())
                .addValue("nombre",                  q.getNombre())
                .addValue("descripcion",             q.getDescripcion())
                .addValue("reglas",                  q.getReglas())
                .addValue("fecha_inicio_inscripcion", q.getFechaInicioInscripcion())
                .addValue("fecha_cierre_inscripcion", q.getFechaCierreInscripcion())
                .addValue("modalidad",               q.getModalidad())
                .addValue("tipo_puntuacion",         q.getTipoPuntuacion());

        call.execute(params);
    }

    public void cambiarEstado(Long idQuiniela, String estado) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("CambiarEstadoQuiniela");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id_quiniela", idQuiniela)
                .addValue("estado",      estado);

        call.execute(params);
    }

    public void eliminarQuiniela(Long idQuiniela) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("EliminarQuiniela");

        call.execute(new MapSqlParameterSource().addValue("id_quiniela", idQuiniela));
    }

    // ── Inscripciones ─────────────────────────────────────────────


    public void inscribirUsuario(Long idUsuario, Long idQuiniela, boolean aceptoReglas) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("InscribirUsuario");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id_usuario",   idUsuario)
                .addValue("id_quiniela",  idQuiniela)
                .addValue("acepto_reglas", aceptoReglas ? 1 : 0);

        call.execute(params);
    }


    public List<Map<String, Object>> obtenerInscripcionesPorQuiniela(Long idQuiniela) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ObtenerInscripcionesPorQuiniela")
                .returningResultSet("inscripciones", (rs, rn) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id_inscripcion",  rs.getLong("id_inscripcion"));
                    row.put("id_usuario",      rs.getLong("id_usuario"));
                    row.put("id_quiniela",     rs.getLong("id_quiniela"));
                    row.put("fecha_inscripcion", rs.getTimestamp("fecha_inscripcion"));
                    row.put("acepto_reglas",   rs.getBoolean("acepto_reglas"));
                    return row;
                });

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id_quiniela", idQuiniela);

        Map<String, Object> result = call.execute(params);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lista =
                (List<Map<String, Object>>) result.getOrDefault("inscripciones", Collections.emptyList());
        return lista;
    }


    static class QuinielaRowMapper implements RowMapper<Quiniela> {
        @Override
        public Quiniela mapRow(ResultSet rs, int rowNum) throws SQLException {
            Quiniela q = new Quiniela();
            q.setIdQuiniela(rs.getLong("id_quiniela"));
            q.setNombre(rs.getString("nombre"));
            q.setDescripcion(rs.getString("descripcion"));
            q.setReglas(rs.getString("reglas"));

            Date fi = rs.getDate("fecha_inicio_inscripcion");
            if (fi != null) q.setFechaInicioInscripcion(fi.toLocalDate());

            Date fc = rs.getDate("fecha_cierre_inscripcion");
            if (fc != null) q.setFechaCierreInscripcion(fc.toLocalDate());

            q.setEstado(rs.getString("estado"));
            q.setModalidad(rs.getString("modalidad"));
            q.setTipoPuntuacion(rs.getString("tipo_puntuacion"));
            return q;
        }
    }
}
