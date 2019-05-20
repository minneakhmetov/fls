package com.jetbrains.repositories;

import com.jetbrains.models.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static com.jetbrains.models.Update.from;

@Component
public class UpdateRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String SELECT = "SELECT * FROM user_update WHERE user_login = ?";

    private static final String INSERT = "INSERT INTO user_update (user_login, last_update_date, user_action) VALUES (?, ?, ?)";

    private RowMapper<Update> rowMapper = new RowMapper<Update>() {
        @Override
        public Update mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Update.builder()
                    .login(rs.getString("user_login"))
                    .time(rs.getTimestamp("last_update_date").toLocalDateTime())
                    .action(from(rs.getString("user_action")))
                    .build();
        }
    };

    public void save(Update update){
        jdbcTemplate.update(INSERT, update.getLogin(), Timestamp.valueOf(update.getTime()), update.getAction().toString());
    }

    public List<Update> read(String login){
        return jdbcTemplate.query(SELECT, rowMapper, login);
    }
}
