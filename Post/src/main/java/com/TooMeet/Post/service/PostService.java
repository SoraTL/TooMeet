package com.TooMeet.Post.service;

import com.TooMeet.Post.entity.Post;
import com.TooMeet.Post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    @Autowired
    PostRepository postRepository;
    public Post newPost(Post post){
        return postRepository.save(post);
    }
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }
    public Post findById(UUID id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }
    public void deletePost(UUID id){ postRepository.delete(postRepository.findById(id).orElse(null));}
    public List<Post> getPostsByAuthorId(Long userId){
        return postRepository.findByAuthorId(userId);
    }

}
