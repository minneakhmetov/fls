package com.jetbrains.repositories;

import com.jetbrains.exceptions.AlreadyRegisteredException;
import com.jetbrains.models.Auth;
import com.jetbrains.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SELECT = "SELECT * FROM user_profile WHERE login = ?";

    private static final String INSERT = "INSERT INTO user_profile (login, password, last_update_date) VALUES (?, ?, ?)";

    private static final String DELETE = "DELETE FROM user_profile WHERE login = ?";

    private static final String UPDATE_PASSWORD = "UPDATE user_profile SET password = ?, last_update_date = ? where login = ?";

    private static final String UPDATE_LAST_UPDATE_TIME = "UPDATE user_profile SET last_update_date = ? where login = ?";

    public void save(User user) throws AlreadyRegisteredException {
        try {
            jdbcTemplate.update(INSERT, user.getLogin(), user.getHashPassword(), Timestamp.valueOf(user.getLastUpdateTime()));
        } catch (DuplicateKeyException e){
            throw new AlreadyRegisteredException();
        }
    }

    public void delete(String login){
        jdbcTemplate.update(DELETE, login);
    }

    private RowMapper<User> rowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return User.builder()
                    .login(rs.getString("login"))
                    .hashPassword(rs.getString("password"))
                    .lastUpdateTime(rs.getTimestamp("last_update_date").toLocalDateTime())
                    .build();
        }
    };

    public Optional<User> read(String login){
        try {
            return Optional.of(jdbcTemplate.queryForObject(SELECT, rowMapper, login));
        } catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    public void updatePassword(User user){
        jdbcTemplate.update(UPDATE_PASSWORD, user.getHashPassword(), Timestamp.valueOf(user.getLastUpdateTime()), user.getLogin());
    }

    public void updateLastUpdateTime(LocalDateTime dateTime, String login){
        jdbcTemplate.update(UPDATE_LAST_UPDATE_TIME, Timestamp.valueOf(dateTime), login);
    }


}
