package com.gemini.LMS.service;

import com.gemini.LMS.dao.BooksDao;
import com.gemini.LMS.dao.CatalogueDao;
import com.gemini.LMS.model.AddBook;
import com.gemini.LMS.model.Book;
import com.gemini.LMS.model.LibraryCatalogue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public final class LibraryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryService.class);

    private final BooksDao booksDao;
    private final CatalogueDao catalogueDao;

    public LibraryService(final BooksDao booksDao, final CatalogueDao catalogueDao) {
        this.booksDao = booksDao;
        this.catalogueDao = catalogueDao;
    }

    public Book fetchBooksInfo(final Integer bookId, final String authorName, final String publishedBy) {
        LOGGER.info("Fetching book info for book with id -> {}, author ->{} and publisher -> {}",
                bookId, authorName, publishedBy);
        return booksDao.fetchBooksInfoFromDb(bookId, authorName, publishedBy);
    }

    @Transactional
    public void persistBook(final AddBook addBook) {
        Integer bookId = booksDao.saveBookInfo(addBook);
        catalogueDao.saveBooksCatalogMapping(bookId, addBook.getCatalogueId());
        LOGGER.info("Book saved successfully -> {}", addBook);
    }

    public List<LibraryCatalogue> getCatalogues(final String sortBy, final Integer top, final Integer skip) {
        return catalogueDao.getCataloguesForDb(sortBy, top, skip);
    }

    @Transactional
    public void removeBook(final Integer bookId) {
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> booksDao.markBookDeleted(bookId)),
                CompletableFuture.runAsync(() -> catalogueDao.removeBookCatalogueMapping(bookId))
        ).join();
    }
}
