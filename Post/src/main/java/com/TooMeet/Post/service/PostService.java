package com.TooMeet.Post.service;

import com.TooMeet.Post.amqp.group.messsage.Choice;
import com.TooMeet.Post.entity.Post;
import com.TooMeet.Post.entity.Reaction;
import com.TooMeet.Post.repository.PostRepository;
import com.TooMeet.Post.repository.ReactionRepository;
import com.TooMeet.Post.request.Format;
import com.TooMeet.Post.request.Image;
import com.TooMeet.Post.request.User;
import com.TooMeet.Post.response.AuthorDto;
import com.TooMeet.Post.response.GroupPostResponse;
import com.TooMeet.Post.response.OriginPostResponse;
import com.TooMeet.Post.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.*;

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
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(post -> convertToResponse(post,userId));
    }
    public Page<PostResponse> getPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findAll(pageable);
        return postPage.map(post -> convertToResponse(post));
    }


    public PostResponse convertToResponse(Post post) {
        PostResponse postResponse = new PostResponse();
        postResponse.setId(post.getId());
        String authorUrl = authorServiceUrl + "/users/info/" + post.getAuthorId();
        User author = restTemplate.getForObject(authorUrl, User.class);
//        User author =new User(2L,"asdfzc",new User.profile(new Image("asdzcxv", Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));
        if(post.getOriginPost()!= null) {
            String userUrl = authorServiceUrl + "/users/info/" + post.getOriginPost().getAuthorId();
            User user = restTemplate.getForObject(userUrl, User.class);
//            User user =new User(2L,"asdfzc",new User.profile(new Image("asdzcxv", Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));
            OriginPostResponse originPostResponse= new OriginPostResponse();
            originPostResponse=new OriginPostResponse().convertToOriginPostResponse(post.getOriginPost());
            originPostResponse.setAuthor(new AuthorDto().convertToAuthor(user));
            postResponse.setOriginPost(originPostResponse);
        }
        postResponse.getAuthor().setAvatar(author.getAvatar());
        postResponse.getAuthor().setName(author.getName());
        postResponse.getAuthor().setId(author.getId());
        postResponse.setContent(post.getContent()==null?"": post.getContent() );
        postResponse.setPrivacy(post.getPrivacy());
        postResponse.setImages(post.getImages());
        postResponse.setReactionCount(post.getReactionCount());
        postResponse.setCreatedAt(post.getCreatedAt());
        postResponse.setUpdatedAt(post.getUpdatedAt());
        postResponse.setCommentCount(post.getCommentCount());
        return postResponse;
    }

    public PostResponse convertToResponse(Post post,Long userId){
        PostResponse postResponse = convertToResponse(post);
        if(userId.equals(post.getAuthorId()))postResponse.setIsAuthor();
        Reaction reaction = reactionRepository.getByPostIdAndUserId(post.getId(),userId);
        if (reaction!= null){
            postResponse.setEmoji(reaction.getEmoji());
        }
        else postResponse.setEmoji(-1);
        return postResponse;
    }

    public Page<PostResponse> getPostsByAuthor(int page, int size , Long authorId, Long userId){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findByAuthorId(authorId,pageable);
        return  postPage.map(post -> convertToResponse(post,userId));
    }

    public Page<PostResponse> getPostsByAuthorIdAndPublic(int page, int size, Long authorId, Long userId){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findByAuthorIdAndPrivacy(authorId,0,pageable);
        return postPage.map(post -> convertToResponse(post,userId));
    }

    public Page<PostResponse> getGroupPosts(int page, int size, Long userId,UUID groupId){
        Pageable pageable =PageRequest.of(page,size,Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findByGroupIdAndStatus(groupId, Choice.accepted,pageable);
        return postPage.map(post -> convertToResponse(post,userId));
    }

    public Post findById(UUID id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        return optionalPost.orElse(null);
    }

    public void deletePost(UUID id){ postRepository.delete(postRepository.findById(id).orElse(null));}

    public List<Post> getPostsByListId(List<UUID> postIds){
        List<Post> posts = new ArrayList<>();
        for(UUID postId : postIds){
            Post post = postRepository.findById(postId).orElse(null);
            posts.add(post);
        }

        return posts;
    }

    public List<Post> getPostByListUserId(List<Long> userIds){

        List<Post> posts = new ArrayList<>();

        for(Long userId:userIds){
            posts.add(postRepository.findOneByAuthorId(userId));
        }

        return posts;

    }

}
