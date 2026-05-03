package com.demo.repository;

import com.demo.client.PostClient;
import com.demo.dto.PostDTO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@ConditionalOnProperty(name = "post.mode", havingValue = "real", matchIfMissing = true)
public class RealPostRepository implements PostRepository {

    private final PostClient client;

    public RealPostRepository(PostClient client) {
        this.client = client;
    }

    @Override
    public List<PostDTO> findAll() {
        return client.fetchAll();
    }

    @Override
    public PostDTO findById(int id) {
        return client.fetchById(id);
    }
}
