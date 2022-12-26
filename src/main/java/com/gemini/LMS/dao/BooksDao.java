package com.gemini.LMS.dao;

import com.gemini.LMS.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public final class BooksDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(BooksDao.class);
    private static final String BOOK_ID_PARAM = "bookId";
    public static final String REMOVE_BOOK = "UPDATE Book SET IsDeleted=true AND BookId=:" + BOOK_ID_PARAM;
    private static final String AUTHOR_NAME_PARAM = "authorName";
    private static final String PUBLISHED_BY_PARAM = "publishedBy";
    private static final String BOOK_NAME_PARAM = "bookName";
    private static final String GET_BOOKS_INFO = "Select * FROM Book WHERE BookId=:" + BOOK_ID_PARAM + " AND " +
            "AuthorName=:" + AUTHOR_NAME_PARAM + " AND PublishedBy=:" + PUBLISHED_BY_PARAM;
    private static final String ADD_BOOK = "INSERT INTO Book (BookName,AuthorName,PublishedBy) VALUES(" +
            ":" + BOOK_NAME_PARAM +
            ":" + AUTHOR_NAME_PARAM +
            ":" + PUBLISHED_BY_PARAM + " RETURNING BookId";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public BooksDao(final NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public Book fetchBooksInfoFromDb(final Integer bookId, final String authorName, final String publishedBy) {
        StringBuilder sb = new StringBuilder(GET_BOOKS_INFO);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(BOOK_ID_PARAM, bookId)
                .addValue(AUTHOR_NAME_PARAM, authorName)
                .addValue(PUBLISHED_BY_PARAM, publishedBy);

        try {
            LOGGER.info("Fetching books info with params {}", params);
            return namedParameterJdbcTemplate.queryForObject(sb.toString(), params,
                    BeanPropertyRowMapper.newInstance(Book.class));
        } catch (EmptyResultDataAccessException e) {
            return null;
        } catch (Exception e) {
            LOGGER.error("Failed to fetch books info - {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

    public Integer saveBookInfo(Book addBook) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(BOOK_NAME_PARAM, addBook.getBookName())
                .addValue(AUTHOR_NAME_PARAM, addBook.getAuthorName())
                .addValue(PUBLISHED_BY_PARAM, addBook.getPublishedBy());
        try {
            LOGGER.info("Saving book record with params {}", params);
            Integer bookId = namedParameterJdbcTemplate.queryForObject(ADD_BOOK, params, Integer.class);
            LOGGER.info("Saved book record with bookId -> {}", bookId);
            return bookId;
        } catch (Exception e) {
            LOGGER.error("Failed to save book record - {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void markBookDeleted(final Integer bookId) {
        try {
            MapSqlParameterSource param = new MapSqlParameterSource()
                    .addValue(BOOK_ID_PARAM, bookId);
            LOGGER.info("Marking book deleted with params -> {}", param);
            Integer count = namedParameterJdbcTemplate.update(REMOVE_BOOK, param);
            if (count > 0)
                LOGGER.info("Book has been marked deleted successfully");
        } catch (Exception e) {
            LOGGER.error("Failed to mark book deleted -> {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
}
