package com.jetbrains.repositories;

import com.jetbrains.models.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreatorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Component
public class AuthRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SELECT = "SELECT * FROM auth WHERE user_login = ?";

    private static final String SELECT_ONE = "SELECT * FROM auth WHERE user_login = ? AND token = ?";

    private static final String SELECT_ONE_BY_LOGIN = "SELECT * FROM auth WHERE user_login = ?";

    private static final String INSERT = "INSERT INTO auth (user_login, token) VALUES (?, ?)";

    private static final String DELETE = "DELETE FROM auth WHERE user_login = ? AND token = ?";

    public void save(Auth auth) {
        jdbcTemplate.update(INSERT, auth.getLogin(), auth.getToken());
    }

    public void delete(Auth auth) {
        jdbcTemplate.update(DELETE, auth.getLogin(), auth.getToken());
    }

    private RowMapper<Auth> rowMapper = new RowMapper<Auth>() {
        @Override
        public Auth mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Auth.builder()
                    .login(rs.getString("user_login"))
                    .token(rs.getString("token"))
                    .build();
        }
    };

    public List<Auth> read(String login) {
        return jdbcTemplate.query(SELECT, rowMapper, login);
    }

    public Optional<Auth> readOne(Auth auth) {
        try {
            return Optional.of(jdbcTemplate.queryForObject(SELECT_ONE, rowMapper, auth.getLogin(), auth.getToken()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Auth> readOne(String login){
        try {
            return Optional.of(jdbcTemplate.queryForObject(SELECT_ONE_BY_LOGIN, rowMapper, login));
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }
}
