package com.demo.model;

// GraphQL resolves fields by matching this record's accessors to the schema type fields
public record Book(String id, String title, String author, int year, String genre) {}
