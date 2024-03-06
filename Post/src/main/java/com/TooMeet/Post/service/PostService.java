package com.TooMeet.Post.service;

import com.TooMeet.Post.entity.Post;
import com.TooMeet.Post.repository.PostRepository;
import com.TooMeet.Post.resposn.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public Page<PostResponse> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(this::convertToResponse);
    }
    private PostResponse convertToResponse(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());
        postResponse.getAuthor().setName("Bard");
        postResponse.getAuthor().setAvatar("https://avatars.google.com/static/images/1.jpg");
        postResponse.getAuthor().setId(1);
        postResponse.setContent(post.getContent());
        postResponse.setPrivacy(post.getPrivacy());
        postResponse.setEmoji(1);
        postResponse.setCreateAt(post.getCreatedAt());
        postResponse.setUpdateAt(post.getUpdatedAt());
        return postResponse;
    }

    public Post findById(UUID id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    public void deletePost(UUID id){ postRepository.delete(postRepository.findById(id).orElse(null));}

    public List<Post> getPostsByAuthorId(Long userId){
        return postRepository.findByAuthorId(userId);
    }

    public List<Post> getPostsByListId(List<UUID> postIds){
        List<Post> posts = new ArrayList<>();
        for(UUID postId : postIds){
            Post post = postRepository.findById(postId).orElse(null);
            posts.add(post);
        }
        return posts;
    }

}
