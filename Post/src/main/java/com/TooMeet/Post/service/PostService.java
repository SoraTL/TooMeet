package com.TooMeet.Post.service;

import com.TooMeet.Post.entity.Post;
import com.TooMeet.Post.entity.Reaction;
import com.TooMeet.Post.repository.PostRepository;
import com.TooMeet.Post.repository.ReactionRepository;
import com.TooMeet.Post.request.User;
import com.TooMeet.Post.resposn.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    PostRepository postRepository;

    @Autowired
    ReactionRepository reactionRepository;
    @Value("${author.service.url}")
    private String authorServiceUrl;
    public Post newPost(Post post){
        return postRepository.save(post);
    }

    public Page<PostResponse> getPosts(int page, int size, Long userId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(post -> convertToResponse(post,userId));
    }
    public PostResponse convertToResponse(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());

        String authorUrl = authorServiceUrl + "/" + post.getAuthorId();
        User author = restTemplate.getForObject(authorUrl, User.class);

        postResponse.getAuthor().setAvatar(author.getAvatar());
        postResponse.getAuthor().setName(author.getName());
        postResponse.getAuthor().setId(author.getId());

        postResponse.setContent(post.getContent());
        postResponse.setPrivacy(post.getPrivacy());
        postResponse.setReactionCount(post.getReactionCount());
        postResponse.setCreateAt(post.getCreatedAt());
        postResponse.setUpdateAt(post.getUpdatedAt());
        postResponse.setCommentCount(post.getCommentCount());
        return postResponse;
    }

    public PostResponse convertToResponse(Post post,Long userId){
        PostResponse postResponse = convertToResponse(post);
        Reaction reaction = reactionRepository.getByPostIdAndUserId(post.getId(),userId);
        if (reaction!= null){
            postResponse.setEmoji(reaction.getEmoji());
        }
        else postResponse.setEmoji(-1);
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
