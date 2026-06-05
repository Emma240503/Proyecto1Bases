package com.example.proyecto1bases.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class PronosticoService {

    @Autowired
    private DataSource dataSource;


    public void insertarPronostico(Long idUsuario, Long idPartido, Long idQuiniela,
                                   Integer golesLocalPredicho, Integer golesVisitantePredicho) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("InsertarPronostico");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id_usuario",              idUsuario)
                .addValue("id_partido",              idPartido)
                .addValue("id_quiniela",             idQuiniela)
                .addValue("goles_local_predicho",    golesLocalPredicho)
                .addValue("goles_visitante_predicho",golesVisitantePredicho);

        call.execute(params);
    }


    public List<Map<String, Object>> obtenerPronosticosPorUsuario(Long idUsuario) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ObtenerPronosticosPorUsuario")
                .returningResultSet("pronosticos", (rs, rn) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id_pronostico",           rs.getLong("id_pronostico"));
                    row.put("id_partido",              rs.getLong("id_partido"));
                    row.put("id_quiniela",             rs.getLong("id_quiniela"));
                    row.put("goles_local_predicho",    rs.getInt("goles_local_predicho"));
                    row.put("goles_visitante_predicho",rs.getInt("goles_visitante_predicho"));
                    row.put("fecha_hora_ingreso",      rs.getTimestamp("fecha_hora_ingreso"));
                    return row;
                });

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id_usuario", idUsuario);

        Map<String, Object> result = call.execute(params);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lista =
                (List<Map<String, Object>>) result.getOrDefault("pronosticos", Collections.emptyList());
        return lista;
    }


    public List<Map<String, Object>> obtenerRanking(Long idQuiniela) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ObtenerRanking")
                .returningResultSet("ranking", (rs, rn) -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("posicion",       rs.getInt("posicion"));
                    row.put("nombre_completo",rs.getString("nombre_completo"));
                    row.put("username",       rs.getString("username"));
                    row.put("puntos_totales", rs.getInt("puntos_totales"));
                    return row;
                });

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id_quiniela", idQuiniela);

        Map<String, Object> result = call.execute(params);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> lista =
                (List<Map<String, Object>>) result.getOrDefault("ranking", Collections.emptyList());
        return lista;
    }
}
