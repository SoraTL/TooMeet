package com.TooMeet.Post.controller;


import com.TooMeet.Post.entity.Comment;
import com.TooMeet.Post.entity.Post;
import com.TooMeet.Post.repository.PostRepository;
import com.TooMeet.Post.resposn.PostResponse;
import com.TooMeet.Post.service.CommentService;
import com.TooMeet.Post.service.ImageUpload;
import com.TooMeet.Post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @PostMapping("")
    public ResponseEntity<Post> newPost(@RequestHeader(value = "x-user-id", required = false) Long userId,
                                        @RequestParam("content") String content,
                                        @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                        @RequestParam("privacy") int privacy,
                                        @RequestParam(value = "groupId", required = false) Long groupId) {
        try {
            if (userId == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (content == null || content.isEmpty() || privacy < 0 || privacy > 2) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

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
            if (savedPost != null) {
                return new ResponseEntity<>(savedPost, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("")
    public ResponseEntity<Page<PostResponse>> getAll(@RequestHeader(value = "x-user-id", required = false) Long userId,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size){
        {

            Page<PostResponse> posts = postService.getPosts(page, size);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        }
    }
    
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@RequestHeader(value = "x-user-id", required = false) Long userId,
                                           @PathVariable UUID postId,
                                           @RequestParam("content") String content,
                                           @RequestPart("images") List<MultipartFile> images,
                                           @RequestParam("privacy") int privacy ) throws Exception {
        if(postRepository.findById(postId).orElse(null).getAuthorId()==userId)
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
    public ResponseEntity<String> deletePost(@RequestHeader(value = "x-user-id",required = false) Long userId,
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
    public ResponseEntity<Comment> comment(@RequestParam("parentId") UUID parentId,
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
            post.addComment(comment);
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
            return new ResponseEntity<>(comment, HttpStatus.CREATED);
        }
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
