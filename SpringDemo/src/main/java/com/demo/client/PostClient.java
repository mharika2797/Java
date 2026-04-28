package com.demo.client;

import com.demo.dto.PostDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

// Responsible for ALL HTTP communication with the external API.
// The rest of the app never knows where the data comes from.
@Component
public class PostClient {

    private final WebClient webClient;

    public PostClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }

    public List<PostDTO> fetchAll() {
        return webClient.get()
                .uri("/posts")
                .retrieve()
                .bodyToFlux(PostDTO.class)
                .collectList()
                .block();
    }

    public PostDTO fetchById(int id) {
        return webClient.get()
                .uri("/posts/{id}", id)
                .retrieve()
                .bodyToMono(PostDTO.class)
                .block();
    }
}
