package com.TooMeet.Post.controller;


import com.TooMeet.Post.entity.Post;
import com.TooMeet.Post.entity.Reaction;
import com.TooMeet.Post.repository.PostRepository;
import com.TooMeet.Post.resposn.Response;
import com.TooMeet.Post.service.CommentService;
import com.TooMeet.Post.service.ImageUpload;
import com.TooMeet.Post.service.PostService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
                        @RequestParam(value = "images" ,required = false) List<MultipartFile> images,
                        @RequestParam("privacy") int privacy,
                        @RequestParam(value = "groupId", required = false) Long groupId) throws IOException {
        if(userId != null){
            Post post = new Post();
            post.setContent(content);
            post.setImages(new ArrayList<>());
            post.setPrivacy(privacy);
            post.setAuthorId(userId);
            if (!images.isEmpty()){
            List<String> imageUrls = new ArrayList<>();
                for (MultipartFile image : images) {
                    try {
                        String imageUrl = imageUpload.uploadImage(image);
                        post.getImages().add(imageUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return new ResponseEntity<>(postService.newPost(post),HttpStatus.CREATED);
//            amqpService.sendNewPostMessage("New post created");
        }
        else{
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("")
    public ResponseEntity<Response> getAll(@RequestHeader(value = "x-user-id", required = false) Long userId,
                             @RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "3") int limit){
        {
            Response response= new Response();
            Page<Post> posts = postRepository.findAll(PageRequest.of(page,limit));
            response.setBody(posts.getContent());
            response.setAllPage(posts.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
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

//    @PostMapping("/reaction")
//    public ResponseEntity<Reaction> like

    @GetMapping("/group")
    public ResponseEntity<List<Post>> groupPosts(){

        return null;
    }

}
