package com.demo.service;

import com.demo.client.PostClient;
import com.demo.dto.PostDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostClient client;

    public PostService(PostClient client) {
        this.client = client;
    }

    public List<PostDTO> getAllPosts() {
        return client.fetchAll();
    }

    public PostDTO getPost(int id) {
        return client.fetchById(id);
    }
}
