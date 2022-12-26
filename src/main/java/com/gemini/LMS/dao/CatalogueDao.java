package com.gemini.LMS.dao;

import com.gemini.LMS.model.LibraryCatalogue;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public final class CatalogueDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogueDao.class);

    private static final String CATALOGUE_ID_PARAM = "catalogueId";
    private static final String BOOK_ID_PARAM = "bookId";
    public static final String REMOVE_BOOK_CATALOGUE_MAPPING = "DELETE FROM BookCatalogueMapping WHERE BookId:"
            + BOOK_ID_PARAM;
    private static final String LIMIT_PARAM = "limit";
    private static final String OFFSET_PARAM = "offset";
    private static final String SAVE_CATALOGUE_BOOK_MAPPING = "INSERT INTO CatalogueBookMapping(CatalogueId,BookId) VALUES (" +
            ":" + CATALOGUE_ID_PARAM + "" +
            ":" + BOOK_ID_PARAM + ")";
    private static final String GET_CATALOGUE_DETAIL = "SELECT c.CatalogueId, c.CatalogueName, b.BookName, b.Author ," +
            "b.BookId, b.PublishedBy FROM  Catalogue c" +
            "INNER JOIN CatalogueBookMapping cbm on cbm.CatalogueId=c.CatalogueId " +
            "INNER JOIN Book b on b.BookId=cbm.BookId";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public CatalogueDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void saveBooksCatalogMapping(final Integer bookId, final Integer catalogId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(CATALOGUE_ID_PARAM, catalogId)
                .addValue(BOOK_ID_PARAM, bookId);
        try {
            LOGGER.info("Saving catalog and book mapping with params -> {}", params);
            Integer row = namedParameterJdbcTemplate.update(SAVE_CATALOGUE_BOOK_MAPPING, params);
            LOGGER.info("Saving catalog and book mapping with result -> {}", row);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public List<LibraryCatalogue> getCataloguesForDb(String sortBy, Integer top, Integer skip) {
        StringBuilder sb = new StringBuilder(GET_CATALOGUE_DETAIL);
        if (!sortBy.isBlank())
            sb.append(" ORDER BY ").append(sortBy);
        sb.append(" LIMIT :" + LIMIT_PARAM + " OFFSET :" + OFFSET_PARAM);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(OFFSET_PARAM, skip)
                .addValue(LIMIT_PARAM, top);
        try {
            LOGGER.info("Fetching catalogues data from DB with params-> {}", params);
            return namedParameterJdbcTemplate.query(sb.toString(), params,
                    BeanPropertyRowMapper.newInstance(LibraryCatalogue.class));
        } catch (Exception e) {
            LOGGER.error("Failed to fetch catalogues data from DB -> {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void removeBookCatalogueMapping(final Integer bookId) {
        try {
            MapSqlParameterSource params = new MapSqlParameterSource()
                    .addValue(BOOK_ID_PARAM, bookId);
            LOGGER.info("Removing Book catalogue mapping with params -> {}", params);
            Integer count = namedParameterJdbcTemplate.update(REMOVE_BOOK_CATALOGUE_MAPPING, params);
            if (count > 0)
                LOGGER.info("Successfully removed book with Id -> {} ", bookId);
        } catch (Exception e) {
            LOGGER.error("Failed to remove book catalogue mapping -> {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
