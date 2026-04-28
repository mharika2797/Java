package com.demo.controller;

import com.demo.dto.PostDTO;
import com.demo.service.PostService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ─────────────────────────────────────────────────────────────────────────────
//  CONTROLLER 2  —  External API call  →  JSONPlaceholder (free, no API key)
//
//  Architecture:  PostController → PostService → PostClient → External API
//                                                     ↑
//                                              WebClient (HTTP)
//
//  The FE only sees clean PostDTO objects — it never knows we're calling
//  a third-party API. If we ever swap the API, only PostClient changes.
//
//  Endpoints:
//    GET  /api/posts       → list 100 posts from jsonplaceholder.typicode.com
//    GET  /api/posts/{id}  → get one post by ID (1–100)
// ─────────────────────────────────────────────────────────────────────────────
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService service;

    public PostController(PostService service) {
        this.service = service;
    }

    @GetMapping
    public List<PostDTO> getAll() {
        return service.getAllPosts();
    }

    @GetMapping("/{id}")
    public PostDTO getById(@PathVariable int id) {
        return service.getPost(id);
    }
}
