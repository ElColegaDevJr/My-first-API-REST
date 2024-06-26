package com.example.obrestdatajpa.controller;

import com.example.obrestdatajpa.entities.Book;
import com.example.obrestdatajpa.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final Logger log = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookRepository bookRepository;

    // Constructor
    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // CRUD sobre la entidad Book

    /**
     * Buscar todos los libros que hay en BBDD (ArrayList de libros)
     * http://localhost:8081/api/books
     * @return List<Book>
     */
    @GetMapping
    public List<Book> findAll() {
        // Recuperar y devolver los libros de base de datos
        return bookRepository.findAll();
    }

    /**
     * Buscar un libro por ID
     * http://localhost:8081/api/books/1
     * @param id Long
     * @return ResponseEntity<Book>
     */
    @GetMapping("/{id}")
    public ResponseEntity<Book> findOneById(@PathVariable Long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        return bookOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Crear un nuevo libro en la BBDD
     * @param book Book
     * @return ResponseEntity<Book>
     */
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        if (book.getId() != null) { // El libro ya existe
            log.warn("Intentando crear un libro con id existente");
            return ResponseEntity.badRequest().build();
        }
        Book result = bookRepository.save(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Actualizar un libro existente en la BBDD
     * @param book Book
     * @return ResponseEntity<Book>
     */
    @PutMapping
    public ResponseEntity<Book> update(@RequestBody Book book) {
        if (book.getId() == null) { // No se puede actualizar un libro que no tiene id
            log.warn("Intentando actualizar un libro que no existe");
            return ResponseEntity.badRequest().build();
        }
        Book result = bookRepository.save(book);
        return ResponseEntity.ok(result);
    }

    /**
     * Borrar un libro por ID
     * @param id Long
     * @return ResponseEntity<Void>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            log.warn("Intentando borrar un libro que no existe");
            return ResponseEntity.notFound().build();
        }
        bookRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}





