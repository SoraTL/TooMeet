package com.TooMeet.Post.controller;


import com.TooMeet.Post.entity.Comment;
import com.TooMeet.Post.entity.CommentReaction;
import com.TooMeet.Post.entity.Post;
import com.TooMeet.Post.entity.Reaction;
import com.TooMeet.Post.repository.CommentReactionRepository;
import com.TooMeet.Post.repository.CommentRepository;
import com.TooMeet.Post.repository.PostRepository;
import com.TooMeet.Post.repository.ReactionRepository;
import com.TooMeet.Post.request.*;
import com.TooMeet.Post.resposn.CommentResponse;
import com.TooMeet.Post.resposn.PostResponse;
import com.TooMeet.Post.resposn.ReactionResponse;
import com.TooMeet.Post.service.CommentService;
import com.TooMeet.Post.service.ImageUpload;
import com.TooMeet.Post.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    @Autowired
    CommentReactionRepository commentReactionRepository;


    RestTemplate restTemplate = new RestTemplate();

    @Value("${author.service.url}")
    private String userUrl;

    private final String groupUrl = "";





    @PostMapping("")
    public ResponseEntity<PostResponse> newPost(@RequestHeader(value = "x-user-id") Long userId,
                                        @RequestParam(value = "content", required = false) String content,
                                        @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                        @RequestParam("privacy") Integer privacy,
                                        @RequestParam(value = "groupId", required = false) Long groupId) {
        try {
            String Url= userUrl + "/users/info/" + userId.toString();
            if  ((content == null && ( images!=null && images.get(0).isEmpty()) ) || privacy < 0 || privacy > 3 || (content!=null && content.length()>5000) || (images!=null&& images.size()>5)) {
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
            response.setImages(post.getImages());
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
    public ResponseEntity<Page<PostResponse>> getAll(@RequestHeader(value = "x-user-id") Long userId,
                                                     @RequestParam(defaultValue = "0") Integer page,
                                                     @RequestParam(defaultValue = "10") Integer limit){
        {

            Page<PostResponse> posts = postService.getPosts(page, limit,userId);
            return new ResponseEntity<>(posts, HttpStatus.OK);
        }
    }

    @GetMapping("/guest")
    public ResponseEntity<Page<PostResponse>> getAllByGuest(@RequestParam(defaultValue = "0") Integer page,
                                                            @RequestParam(defaultValue = "10") Integer limit){
        {
            Pageable pageable= PageRequest.of(page,limit);
            Page<PostResponse> posts = postService.getPosts(page,limit);
            return new ResponseEntity<>(posts, HttpStatus.OK);

        }
    }

    @PostMapping("/{id}/share") ResponseEntity<PostResponse> sharePost(@RequestHeader(value = "x-user-id") Long userId,
                                                                       @PathVariable(value = "id") UUID postId,
                                                                       @RequestParam(value = "content" ,required = false)String content ){
        Post sharedPost=postRepository.findById(postId).orElse(null);

        Post sharePost = new Post();

        sharePost.setOriginPost(sharedPost);

        return new ResponseEntity<>(HttpStatus.CREATED);



    }


    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@RequestHeader(value = "x-user-id") Long userId,
                                           @PathVariable UUID postId,
                                           @RequestParam("content") String content,
                                           @RequestPart("images") List<MultipartFile> images,
                                           @RequestParam("privacy") Integer privacy ) throws Exception {
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
    public ResponseEntity<CommentResponse> comment(@RequestHeader(value = "x-user-id") Long userId,
                                           @PathVariable(value = "id") UUID postId,
                                           @RequestBody NewCommentModel commentModel){
        String Url= userUrl + "/users/info/" + userId.toString();
        Post post = postService.findById(postId);
//        User user = restTemplate.getForObject(Url, User.class, userId);
        User user = new User();
        user.setId(123L);
        user.setName("lahjkdshf");
        if(user == null )return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Comment comment = new Comment();
        comment.setContent(commentModel.getContent());
        comment.setUserId(userId);
        comment.setPost(post);
        comment.setLevel(0);
        comment.setParentId(postId);

        post.getComments().add(comment);
        post.setCommentCount(post.getCommentCount()+1);
        postRepository.save(post);
        List<Comment> comments=post.getComments();
        Comment savedComment=comments.get(comments.size()-1);

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(savedComment.getId());
        commentResponse.getAuthor().setId(userId);
        commentResponse.getAuthor().setName(user.getName());
        commentResponse.getAuthor().setAvatar(user.getAvatar());
        commentResponse.setContent(savedComment.getContent());
        commentResponse.setCreatedAt(savedComment.getCreatedAt());
        commentResponse.setParentId(savedComment.getParentId());
        commentResponse.setLevel(savedComment.getLevel());

        return new ResponseEntity<>(commentResponse, HttpStatus.CREATED);

    }

    @PostMapping("/{id}/comments/reply")
    public ResponseEntity<CommentResponse> replyComment(@RequestHeader(value = "x-user-id") Long userId,
                                                        @PathVariable(value = "id") UUID postId,
                                                        @RequestBody NewReplyModel replyModel){
        String Url= userUrl + "/users/info/" + userId.toString();
        Post post = postService.findById(postId);
//        User user = restTemplate.getForObject(Url, User.class, userId);
        User user = new User();
        user.setId(123L);
        user.setName("lahjkdshf");
        if(user == null )return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        Comment comment = commentRepository.findById(replyModel.getParentId()).orElse(null);
        if(comment==null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Comment reply = new Comment();
        if(comment.getLevel()==2){
            reply.setLevel(2);
            reply.setParentId(comment.getParentId());
        }
        else {
            reply.setParentId(replyModel.getParentId());
            reply.setLevel(comment.getLevel() + 1);
        }
        reply.setContent(replyModel.getContent());
        reply.setUserId(userId);
        reply.setPost(post);

        post.getComments().add(reply);
        post.setCommentCount(post.getCommentCount()+1);
        postRepository.save(post);

        List<Comment> comments=post.getComments();
        Comment savedComment=comments.get(comments.size()-1);

        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(savedComment.getId());
        commentResponse.getAuthor().setId(userId);
        commentResponse.getAuthor().setName(user.getName());
        commentResponse.getAuthor().setAvatar(user.getAvatar());
        commentResponse.setContent(savedComment.getContent());
        commentResponse.setCreatedAt(savedComment.getCreatedAt());
        commentResponse.setParentId(savedComment.getParentId());
        commentResponse.setLevel(savedComment.getLevel());

        return new ResponseEntity<>(commentResponse, HttpStatus.CREATED);

    }

//    @DeleteMapping("/{id}")

    @GetMapping("/{id}/comments")
    public ResponseEntity<Page<CommentResponse>> getComment(
                                                      @RequestHeader(value = "x-user-id",required = false) Long userId,
                                                      @RequestParam(value = "parentId",required = false) UUID parentId,
                                                      @RequestParam(value = "page",defaultValue = "0")Integer page,
                                                      @RequestParam(value = "limit",defaultValue = "5") Integer limit,
                                                      @PathVariable("id") UUID postId){
        if(parentId==null){
            return new ResponseEntity<>(commentService.getCommentsByParentId(postId,page,limit,userId),HttpStatus.OK);
        }
        Comment comment = commentRepository.findById(parentId).orElse(null);
        Page<CommentResponse> comments = commentService.getCommentsByParentId(parentId,page,limit,userId);
        return new ResponseEntity<>(comments,HttpStatus.OK);
    }


    // Post Reaction
    @PutMapping("/{id}/reaction")
    public ResponseEntity<ReactionResponse> reaction(@RequestHeader(value = "x-user-id") Long userId,
                                                     @PathVariable("id") UUID postId,
                                                     @RequestBody NewReactionModel reactionModel ){
        ReactionResponse response = new ReactionResponse();
        if(reactionModel.getEmoji()>5 || reactionModel.getEmoji()<0) {
            response.setMassage("Emoji nằm trong khỏang 0 đến 5");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Post post;
        post = postService.findById(postId);
        if(post==null) {
            response.setMassage("Không tìm thấy bài viết " + postId.toString());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        Reaction reaction=reactionRepository.getByPostIdAndUserId(postId,userId);
        if(reaction!=null){
            reaction.setEmoji(reactionModel.getEmoji());
            reactionRepository.save(reaction);
            response.setMassage("Đã cập nhật tương tác!");
            response.setReactionCount(post.getReactionCount());
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

        Reaction newReaction = new Reaction();
        newReaction.setEmoji(reactionModel.getEmoji());
        newReaction.setUserId(userId);

        post.getReactions().add(newReaction);
        newReaction.setPost(post);
        post.setReactionCount(post.getReactionCount()+1);
        postRepository.save(post);
        response.setMassage("Tương tác thành công!");
        response.setReactionCount(post.getReactionCount());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @Transactional
    @DeleteMapping("/{id}/reaction")
    public ResponseEntity<ReactionResponse> deteleReaction(@RequestHeader(value = "x-user-id") Long userId,
                                 @PathVariable("id") UUID postId){
        Reaction reaction=reactionRepository.getByPostIdAndUserId(postId,userId);
        ReactionResponse response= new ReactionResponse();
        if(reaction == null){
            response.setMassage("Bạn chưa tương tác bài viết này!");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(!reaction.getUserId().equals(userId)){
            response.setMassage("Bạn không thể xóa tương tác này");
            return new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
        }
        Post post = postRepository.findById(postId).orElse(null);
        if(post==null) {
            response.setMassage("Không tìm thấy bài viết " + postId.toString());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        reactionRepository.deleteReactionByPostIdAndUserId(postId,userId);
        post.setReactionCount(post.getReactionCount()-1);
        postRepository.save(post);
        response.setMassage("Xóa tương tác thành công!");
        response.setReactionCount(post.getReactionCount());
        return new ResponseEntity<>(response,HttpStatus.OK);

    }

    //Comment Reaction
    @PostMapping("/{id}/commentReaction")
    public ResponseEntity<ReactionResponse> commentReaction(@RequestHeader(value = "x-user-id") Long userId,
                                                  @PathVariable("id") UUID commentId,
                                                  @RequestBody NewReactionModel reactionModel){
        ReactionResponse response = new ReactionResponse();
        if(reactionModel.getEmoji()>5 || reactionModel.getEmoji()<0) {
            response.setMassage("Emoji nằm trong khỏang 0 đến 5");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Comment comment;
        comment = commentRepository.findById(commentId).orElse(null);
        if(comment==null) {
            response.setMassage("Không tìm thấy binh luan " + commentId.toString());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        CommentReaction reaction = commentReactionRepository.getByCommentIdAndUserId(commentId,userId);
        if(reaction!=null){
            reaction.setEmoji(reactionModel.getEmoji());
            commentReactionRepository.save(reaction);
            response.setMassage("Đã cập nhật tương tác!");
            response.setReactionCount(comment.getLikeCount());
            return new ResponseEntity<>(response,HttpStatus.OK);
        }

        CommentReaction newReaction = new CommentReaction();
        newReaction.setEmoji(reactionModel.getEmoji());
        newReaction.setUserId(userId);

        comment.getReactions().add(newReaction);
        newReaction.setComment(comment);
        comment.setLikeCount(comment.getLikeCount()+1);
        commentRepository.save(comment);
        response.setMassage("Tương tác thành công!");
        response.setReactionCount(comment.getLikeCount());
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
    
}
