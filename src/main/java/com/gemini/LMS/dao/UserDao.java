package com.gemini.LMS.dao;

import com.gemini.LMS.exception.LibraryException;
import com.gemini.LMS.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDao.class);
    private static final String USERNAME_PARAM = "username";
    private static final String GET_USER_DETAILS = "SELECT * FROM Users WHERE Username=:" + USERNAME_PARAM;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public UserInfo getUserDetails(String username) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(USERNAME_PARAM, username);
        try {
            LOGGER.info("Fetching user with params -> {}", params);
            return namedParameterJdbcTemplate.queryForObject(GET_USER_DETAILS, params, BeanPropertyRowMapper
                    .newInstance(UserInfo.class));
        } catch (EmptyResultDataAccessException e) {
            LOGGER.error("User not exist with username -> {}", username);
            throw new LibraryException(HttpStatus.NOT_FOUND, "User not exist with username ->" + username);
        } catch (Exception e) {
            LOGGER.error("Failed to get user info");
            throw new RuntimeException(e.getMessage());
        }
    }
}
