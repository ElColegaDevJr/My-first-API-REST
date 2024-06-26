package com.example.obrestdatajpa.controller;

import com.example.obrestdatajpa.entities.Book;
import com.example.obrestdatajpa.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {

    @LocalServerPort
    private int port;

    private TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        restTemplateBuilder = restTemplateBuilder.rootUri("http://localhost:" + port);
        testRestTemplate = new TestRestTemplate(restTemplateBuilder);

        // Limpiar la base de datos antes de cada prueba
        bookRepository.deleteAll();

        // Insertar datos de prueba
        Book book1 = new Book();
        book1.setTitle("Libro de Prueba 1");
        book1.setAuthor("Autor de Prueba 1");
        book1.setPages(100);
        book1.setPrice(19.99);
        book1.setReleaseDate(LocalDate.parse("2020-01-01"));
        book1.setOnline(false);

        Book book2 = new Book();
        book2.setTitle("Libro de Prueba 2");
        book2.setAuthor("Autor de Prueba 2");
        book2.setPages(200);
        book2.setPrice(29.99);
        book2.setReleaseDate(LocalDate.parse("2021-01-01"));
        book2.setOnline(true);

        bookRepository.save(book1);
        bookRepository.save(book2);
    }

    @Test
    void hello() {
        ResponseEntity<String> response =
                testRestTemplate.getForEntity("/hola", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Hola mundo!! Hasta luego !", response.getBody());
    }

    @Test
    void findAll() {
        ResponseEntity<Book[]> response =
                testRestTemplate.getForEntity("/api/books", Book[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getStatusCodeValue());

        List<Book> books = Arrays.asList(Objects.requireNonNull(response.getBody()));
        assertFalse(books.isEmpty(), "La lista de libros no debería estar vacía");
        assertTrue(books.size() > 0, "La lista de libros debería tener al menos un elemento");
    }

    @Test
    void findOneById() {
        ResponseEntity<Book> response =
                testRestTemplate.getForEntity("/api/books/1", Book.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void create() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        String json = """
                {
                    "title": "Libro creado desde Spring Test",
                    "author": "Yuval Noah",
                    "pages": 650,
                    "price": 29.99,
                    "releaseDate": "2019-12-01",
                    "online": false
                }
                """;

        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<Book> response = testRestTemplate.exchange("/api/books", HttpMethod.POST, request, Book.class);
        Book result = response.getBody();

        assertNotNull(result);
        assertNotNull(result.getId());  // Verifica que el ID no sea null
        assertEquals("Libro creado desde Spring Test", result.getTitle());
    }
}
