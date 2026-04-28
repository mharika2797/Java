package com.demo.dto;

// Matches the shape of https://jsonplaceholder.typicode.com/posts
// Fields chosen to be FE-friendly (camelCase, clean naming)
public record PostDTO(int id, int userId, String title, String body) {}
