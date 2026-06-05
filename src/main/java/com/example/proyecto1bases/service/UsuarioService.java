package com.example.proyecto1bases.service;

import com.example.proyecto1bases.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ObtenerUsuarioPorUsername")
                .returningResultSet("usuario", new UsuarioRowMapper());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", username);

        Map<String, Object> result = call.execute(params);

        @SuppressWarnings("unchecked")
        List<Usuario> lista = (List<Usuario>) result.get("usuario");

        if (lista == null || lista.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        Usuario u = lista.get(0);
        return User.builder()
                .username(u.getUsername())
                .password(u.getContrasena())
                .authorities(new SimpleGrantedAuthority("ROLE_" + u.getTipoUsuario()))
                .build();
    }


    public void registrarUsuario(Usuario usuario) {
        String hash = passwordEncoder.encode(usuario.getContrasena());

        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("InsertarUsuario");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("nombre_completo", usuario.getNombreCompleto())
                .addValue("username",        usuario.getUsername())
                .addValue("email",           usuario.getEmail())
                .addValue("contrasena",      hash)
                .addValue("fecha_nacimiento",usuario.getFechaNacimiento())
                .addValue("tipo_usuario",    "JUGADOR");

        call.execute(params);
    }

    /** Devuelve todos los usuarios (SP ObtenerUsuarios). */
    public List<Usuario> obtenerTodos() {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ObtenerUsuarios")
                .returningResultSet("usuarios", new UsuarioRowMapper());

        Map<String, Object> result = call.execute();

        @SuppressWarnings("unchecked")
        List<Usuario> lista = (List<Usuario>) result.getOrDefault("usuarios", Collections.emptyList());
        return lista;
    }

    public Usuario obtenerPorUsername(String username) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ObtenerUsuarioPorUsername")
                .returningResultSet("usuario", new UsuarioRowMapper());

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", username);

        Map<String, Object> result = call.execute(params);

        @SuppressWarnings("unchecked")
        List<Usuario> lista = (List<Usuario>) result.get("usuario");
        return (lista != null && !lista.isEmpty()) ? lista.get(0) : null;
    }

    public void actualizarUsuario(Usuario usuario) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("ActualizarUsuario");

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id_usuario",      usuario.getIdUsuario())
                .addValue("nombre_completo", usuario.getNombreCompleto())
                .addValue("email",           usuario.getEmail())
                .addValue("contrasena",      passwordEncoder.encode(usuario.getContrasena()))
                .addValue("fecha_nacimiento",usuario.getFechaNacimiento());

        call.execute(params);
    }

    public void eliminarUsuario(Long idUsuario) {
        SimpleJdbcCall call = new SimpleJdbcCall(dataSource)
                .withProcedureName("EliminarUsuario");

        call.execute(new MapSqlParameterSource().addValue("id_usuario", idUsuario));
    }


    static class UsuarioRowMapper implements RowMapper<Usuario> {
        @Override
        public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getLong("id_usuario"));
            u.setNombreCompleto(rs.getString("nombre_completo"));
            u.setUsername(rs.getString("username"));
            u.setEmail(rs.getString("email"));
            u.setContrasena(rs.getString("contrasena"));
            Date fechaNac = rs.getDate("fecha_nacimiento");
            if (fechaNac != null) u.setFechaNacimiento(fechaNac.toLocalDate());
            u.setTipoUsuario(rs.getString("tipo_usuario"));
            return u;
        }
    }
}
