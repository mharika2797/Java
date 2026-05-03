package com.demo.repository;

import com.demo.dto.PostDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnProperty(name = "post.mode", havingValue = "mock")
public class MockPostRepository implements PostRepository {

    private static final List<PostDTO> MOCK_POSTS = List.of(
            new PostDTO(1, 1, "Mock Post One",   "This is mock body one"),
            new PostDTO(2, 1, "Mock Post Two",   "This is mock body two"),
            new PostDTO(3, 2, "Mock Post Three", "This is mock body three")
    );

    @Override
    public List<PostDTO> findAll() {
        return MOCK_POSTS;
    }

    @Override
    public PostDTO findById(int id) {
        return MOCK_POSTS.stream()
                .filter(p -> p.id() == id)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Mock post not found: " + id));
    }
}
