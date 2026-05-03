package com.demo.repository;

import com.demo.dto.PostDTO;

import java.util.List;

public interface PostRepository {
    List<PostDTO> findAll();
    PostDTO findById(int id);
}
