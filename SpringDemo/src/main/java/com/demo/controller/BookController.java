package com.demo.controller;

import com.demo.model.Book;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// ─────────────────────────────────────────────────────────────────────────────
//  CONTROLLER 3  —  GraphQL
//
//  Key difference from REST:
//    REST  → many endpoints (/books, /books/1, POST /books …)
//    GraphQL → ONE endpoint  POST /graphql  with a query body
//
//  The schema (schema.graphqls) defines what operations are possible.
//  Each method here maps to one operation defined in that schema.
//
//  @QueryMapping   → maps to a `type Query`  field in the schema   (READ)
//  @MutationMapping → maps to a `type Mutation` field in the schema (WRITE)
//  @Argument       → binds a GraphQL argument to a Java parameter
//
//  Test via browser:   http://localhost:8080/graphiql
//  Test via Postman:   POST http://localhost:8080/graphql  (see README for body)
// ─────────────────────────────────────────────────────────────────────────────
@Controller
public class BookController {

    // In-memory store — swap this with a JpaRepository to persist
    private final Map<String, Book> store = new ConcurrentHashMap<>();
    private final AtomicInteger idGen    = new AtomicInteger(3);

    public BookController() {
        store.put("1", new Book("1", "Clean Code",               "Robert C. Martin", 2008, "Programming"));
        store.put("2", new Book("2", "The Pragmatic Programmer",  "David Thomas",     1999, "Programming"));
        store.put("3", new Book("3", "Design Patterns",           "Gang of Four",     1994, "Programming"));
    }

    // ── Queries (READ) ──────────────────────────────────────────────────────

    @QueryMapping                              // handles:  query { books { id title ... } }
    public List<Book> books() {
        return new ArrayList<>(store.values());
    }

    @QueryMapping                              // handles:  query { book(id: "1") { title author } }
    public Book book(@Argument String id) {
        return store.get(id);                  // returns null → GraphQL sends null to the client (schema allows it)
    }

    // ── Mutations (WRITE) ────────────────────────────────────────────────────

    @MutationMapping                           // handles:  mutation { addBook(title:"..." ...) { id title } }
    public Book addBook(@Argument String title,
                        @Argument String author,
                        @Argument int year,
                        @Argument String genre) {
        String id   = String.valueOf(idGen.incrementAndGet());
        Book   book = new Book(id, title, author, year, genre);
        store.put(id, book);
        return book;
    }

    @MutationMapping                           // handles:  mutation { deleteBook(id: "2") }
    public boolean deleteBook(@Argument String id) {
        return store.remove(id) != null;
    }
}
