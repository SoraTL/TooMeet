package com.TooMeet.Post.controller;


import com.TooMeet.Post.entity.Comment;
import com.TooMeet.Post.entity.Post;
import com.TooMeet.Post.entity.Reaction;
import com.TooMeet.Post.repository.CommentRepository;
import com.TooMeet.Post.repository.PostRepository;
import com.TooMeet.Post.repository.ReactionRepository;
import com.TooMeet.Post.request.User;
import com.TooMeet.Post.resposn.CommentResponse;
import com.TooMeet.Post.resposn.PostResponse;
import com.TooMeet.Post.service.CommentService;
import com.TooMeet.Post.service.ImageUpload;
import com.TooMeet.Post.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final ImageUpload imageUpload;
//    private final AMQPService amqpService;

    @Autowired
    PostRepository postRepository;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    ReactionRepository reactionRepository;

    RestTemplate restTemplate = new RestTemplate();

    @Value("${author.service.url}")
    private String userUrl;

    private final String groupUrl = "";

//    {
//        User user = restTemplate.getForObject(userUrl, User.class, userId);
//    }
//    {
//        Group group = restTemplate.getForObject(groupUrl, Group.class, groupId);
//    }



    @PostMapping("")
    public ResponseEntity<PostResponse> newPost(@RequestHeader(value = "x-user-id") Long userId,
                                        @RequestParam(value = "content", required = false) String content,
                                        @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                        @RequestParam("privacy") int privacy,
                                        @RequestParam(value = "groupId", required = false) Long groupId) {
        try {
            String Url= userUrl + "/" + userId.toString();
            if  ((content == null && ( images!=null && images.get(0).isEmpty()) ) || privacy < 0 || privacy > 3 || (content!=null && content.length()>5000)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            User user = restTemplate.getForObject(Url, User.class, userId);
            Post post = new Post();
            post.setContent(content);
            post.setPrivacy(privacy);
            post.setAuthorId(userId);
            if (images!=null && !images.get(0).isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile image : images) {
                    try {
                        String imageUrl = imageUpload.uploadImage(image);
                        imageUrls.add(imageUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                post.setImages(imageUrls);
            }
            Post savedPost = postService.newPost(post);
            PostResponse response = new PostResponse();
            response.convertToResponse(post);
            response.getAuthor().setId(user.getId());
            response.getAuthor().setName(user.getName());
            response.getAuthor().setAvatar(user.getAvatar());
            if (savedPost != null) {
                return new ResponseEntity<>(response, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<Page<PostResponse>> getAll(@RequestHeader(value = "x-user-id",required = false) Long userId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int limit){
        {

            Page<PostResponse> posts = postService.getPosts(page, limit,userId);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        }
    }
    
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@RequestHeader(value = "x-user-id") Long userId,
                                           @PathVariable UUID postId,
                                           @RequestParam("content") String content,
                                           @RequestPart("images") List<MultipartFile> images,
                                           @RequestParam("privacy") int privacy ) throws Exception {
        if(postRepository.findById(postId).orElse(null).getAuthorId() == userId)
        {
            Post existingPost = postService.findById(postId);
            if (existingPost == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            existingPost.setContent(content);
            List<String> existingImageUrls = existingPost.getImages();
            for(String imageUrl:existingImageUrls){
                imageUpload.deleteImageById(imageUpload.extractPublicId(imageUrl));
            }
            List<String> updatedImagesUrls = new ArrayList<>();
            for (MultipartFile image:images){
                try {
                    String uploadedImage= imageUpload.uploadImage(image);
                    updatedImagesUrls.add(uploadedImage);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
            existingPost.setImages(updatedImagesUrls);
            existingPost.setPrivacy(privacy);
            Post updatedPostEntity = postService.newPost(existingPost);

            return new ResponseEntity<>(updatedPostEntity, HttpStatus.OK);
        }
        else {return new ResponseEntity<>(HttpStatus.BAD_REQUEST);}
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(@RequestHeader(value = "x-user-id") Long userId,
            @PathVariable UUID postId) throws Exception {

        if(postRepository.findById(postId).orElse(null).getAuthorId()==userId)
        {
            Post existingPost = postService.findById(postId);
            if (existingPost == null) {
                return new ResponseEntity<>("Không tìm thấy bài viết", HttpStatus.NOT_FOUND);
            }
            List<String> deletedImagesUrls = new ArrayList<>();
            deletedImagesUrls = existingPost.getImages();
            for (String imageUrl : deletedImagesUrls) {
                imageUpload.deleteImageById(imageUpload.extractPublicId(imageUrl));
            }
            postService.deletePost(postId);

            return new ResponseEntity<>("Bài đăng đã xóa thành công!", HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Post>> groupPosts(@RequestParam("postIds") List<UUID> postIds){


        return new ResponseEntity<>(postService.getPostsByListId(postIds),HttpStatus.OK);
    }

    //Comment
    @PostMapping("/{id}/comments")
    public ResponseEntity<Comment> comment(@RequestParam(value = "parentId",required = false) UUID parentId,
                                           @RequestParam("content") String content,
                                           @RequestParam("level") int level,
                                           @RequestHeader(value = "x-user-id",required = false) Long userId,
                                           @PathVariable(value = "id",required = false) UUID postId){
        Post post = postService.findById(postId);
        if (post!=null && userId != null)
        {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setUserId(userId);
            post.getComments().add(comment);
            comment.setPost(post);
            if (level >= 3) {
                comment.setLevel(3);
                comment.setParentId(commentService.getCommentByParentId(parentId).getParentId());
            }
            else if (level == 0) {
                comment.setLevel(level);
                comment.setParentId(postId);
            } else {
                comment.setLevel(level);
                comment.setParentId(parentId);
            }
            Comment savedComment= commentRepository.save(comment);
            return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
        }
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<CommentResponse>> getComment(@RequestParam UUID parentId,
                                                      @RequestHeader(value = "x-user-id",required = false) Long userId,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size,
                                                      @PathVariable("id") UUID postId){

        Page<CommentResponse> comments = commentService.getCommentsByParentId(parentId,page,size);
        return new ResponseEntity<>(comments,HttpStatus.OK);

    }

    @PutMapping("/{id}/reaction")
    public String reaction(@RequestHeader(value = "x-user-id") Long userId,
                                             @PathVariable("id") UUID postId,
                                             @RequestParam("emoji") int emoji ){

        if(emoji>4 || emoji<0)
            return "Emoji nằm trong khỏang 0 đến 4";
        Reaction reaction=reactionRepository.getByPostIdAndUserId(postId,userId);
        if(reaction!=null){
            reaction.setEmoji(emoji);
            reactionRepository.save(reaction);
            return "Đã cập nhật tương tác!";
        }
        Reaction newReaction = new Reaction();
        newReaction.setEmoji(emoji);
        newReaction.setUserId(userId);
        Post post = new Post();
        post = postService.findById(postId);
        if(post==null)
            return "Không tìm thấy bài viết "+ postId.toString();
        post.getReactions().add(newReaction);
        newReaction.setPost(post);
        post.setReactionCount(post.getReactionCount()+1);
        postRepository.save(post);
        return "Tương tác thành công!";

    }

    @Transactional
    @DeleteMapping("/{id}/reaction")
    public String deteleReaction(@RequestHeader(value = "x-user-id") Long userId,
                                 @PathVariable("id") UUID postId){
        Reaction reaction=reactionRepository.getByPostIdAndUserId(postId,userId);
        if(reaction == null){
            return "Bạn chưa tương tác bài viết này!";
        }
        reactionRepository.deleteReactionByPostIdAndUserId(postId,userId);
        Post post = postRepository.findById(postId).orElse(null);
        if(post==null) return "Không tìm thấy bài viết "+ postId.toString();
        post.setReactionCount(post.getReactionCount()-1);
        postRepository.save(post);
        return "Xóa tương tác thành công!";

    }

}
