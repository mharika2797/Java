package com.demo.service;

import com.demo.dto.PostDTO;
import com.demo.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<PostDTO> getAllPosts() {
        return postRepository.findAll();
    }

    public PostDTO getPost(int id) {
        return postRepository.findById(id);
    }
}
