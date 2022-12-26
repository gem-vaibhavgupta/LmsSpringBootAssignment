package com.gemini.LMS.controller;

import com.gemini.LMS.model.AddBook;
import com.gemini.LMS.model.Book;
import com.gemini.LMS.model.LibraryCatalogue;
import com.gemini.LMS.service.LibraryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gemini.LMS.utils.Constants.*;

@RestController
public final class LibraryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryController.class);
    private final LibraryService libraryService;

    public LibraryController(final LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/library/catalogue")
    @PreAuthorize("hasRole('Staff') or hasRole('Admin')")
    public ResponseEntity<List<?>> catalogue(@RequestParam(required = false, defaultValue = EMPTY_STRING) final String sortBy,
                                             @RequestParam(required = false, defaultValue = TOP_DEFAULT_VALUE) final Integer top,
                                             @RequestParam(required = false, defaultValue = SKIP_DEFAULT_VALUE) final Integer skip) {
        List<LibraryCatalogue> catalogueList = libraryService.getCatalogues(sortBy, top, skip);
        LOGGER.info("Fetched Catalogues -> {}", catalogueList);
        return new ResponseEntity<>(catalogueList, HttpStatus.OK);
    }


    @GetMapping("/library")
    @PreAuthorize("hasRole('User') or hasRole('Staff') or hasRole('Admin')")
    public ResponseEntity<Book> getBooksInfo(@RequestParam final Integer bookId,
                                             @RequestParam final String authorName,
                                             @RequestParam final String publishedBy) {
        LOGGER.info("Fetching books info");
        Book bookInfo = libraryService.fetchBooksInfo(bookId, authorName, publishedBy);
        return ResponseEntity.ok(bookInfo);
    }

    @PostMapping("/library")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> postBooks(@RequestBody final AddBook addBook) {
        LOGGER.info("Adding new book {}", addBook);
        libraryService.persistBook(addBook);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/library")
    @PreAuthorize("hasRole('Admin')")
    public ResponseEntity<Void> removeBook(@RequestParam final Integer bookId) {
        LOGGER.info("Removing book ");
        libraryService.removeBook(bookId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
