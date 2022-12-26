package com.gemini.LMS.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gemini.LMS.controller.LibraryController;
import com.gemini.LMS.model.AddBook;
import com.gemini.LMS.model.Book;
import com.gemini.LMS.model.LibraryCatalogue;
import com.gemini.LMS.service.LibraryService;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LibraryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    LibraryService libraryService;

    @InjectMocks
    LibraryController libraryController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(libraryController).build();
        MockitoAnnotations.openMocks(this);
    }


    @Test
    @Order(3)
    void getCatalogues() throws Exception {

        List<LibraryCatalogue> list = new ArrayList<>();
        LibraryCatalogue libraryCatalogue1 = new LibraryCatalogue();
        libraryCatalogue1.setCatalogueId(1);
        libraryCatalogue1.setCatalogueName("Java");
        libraryCatalogue1.setBookId(1);
        libraryCatalogue1.setBookName("Head First Java");
        libraryCatalogue1.setAuthorName("Kathy Sierra & Bert Bates");
        libraryCatalogue1.setPublishedBy("Publisher 1");
        list.add(libraryCatalogue1);

        LibraryCatalogue libraryCatalogue2 = new LibraryCatalogue();
        libraryCatalogue2.setCatalogueId(1);
        libraryCatalogue2.setCatalogueName("Java");
        libraryCatalogue2.setBookId(2);
        libraryCatalogue2.setBookName("Java: A Beginner’s Guide");
        libraryCatalogue2.setAuthorName("Herbert Schildt");
        libraryCatalogue2.setPublishedBy("Publisher 2");
        list.add(libraryCatalogue2);

        when(libraryService.getCatalogues("AuthorName", 20, 0)).thenReturn(list);
        this.mockMvc.perform(get("/library/catalogue"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @Order(2)
    void getBookRecord() throws Exception {
        Book book = new Book();
        book.setBookId(4);
        book.setBookName("Effective Java");
        book.setAuthorName("Joshua Bloch");
        book.setPublishedBy("A Publisher");

        Mockito.doReturn(book).when(libraryService).fetchBooksInfo(anyInt(), anyString(), anyString());

        this.mockMvc
                .perform(get("/library")
                        .param("bookId", "4")
                        .param("authorName", "Joshua Bloch")
                        .param("publisherName", "A Publisher"))
                .andExpect(status().isOk())

                .andExpect(MockMvcResultMatchers.jsonPath(".bookId").value(4))
                .andExpect(MockMvcResultMatchers.jsonPath(".bookName").value("Effective Java"))
                .andExpect(MockMvcResultMatchers.jsonPath(".autherName").value("A Publisher"));

    }

    @Test
    @Order(1)
    void saveBookRecord() throws Exception {
        AddBook book = new AddBook();
        book.setBookId(5);
        book.setBookName("Java for Dummies ");
        book.setAuthorName("Barry A. Burd");
        book.setCatalogueId(1);
        book.setPublishedBy("Publisher 3");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        String jsonBody = mapper.writeValueAsString(book);

        this.mockMvc.perform(post("/library")
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(4)
    void deleteRecordById() throws Exception {
        this.mockMvc.perform(delete("/library")
                        .param("bookId", "2"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}