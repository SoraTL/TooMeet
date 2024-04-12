package com.TooMeet.Post.controller;


import com.TooMeet.Post.amqp.AMQPService;
import com.TooMeet.Post.amqp.notification.message.CommentReactionMessage;
import com.TooMeet.Post.amqp.notification.message.NewCommentMessage;
import com.TooMeet.Post.amqp.notification.message.NewPostMessage;
import com.TooMeet.Post.amqp.notification.message.ReactionPostMessage;
import com.TooMeet.Post.amqp.socket.message.SocketCommentCountMessage;
import com.TooMeet.Post.amqp.socket.message.SocketNewCommentMessage;
import com.TooMeet.Post.amqp.socket.message.SocketReactionPostMessage;
import com.TooMeet.Post.entity.Comment;
import com.TooMeet.Post.entity.CommentReaction;
import com.TooMeet.Post.entity.Post;
import com.TooMeet.Post.entity.Reaction;
import com.TooMeet.Post.repository.CommentReactionRepository;
import com.TooMeet.Post.repository.CommentRepository;
import com.TooMeet.Post.repository.PostRepository;
import com.TooMeet.Post.repository.ReactionRepository;
import com.TooMeet.Post.request.*;
import com.TooMeet.Post.response.*;
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

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

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
    @Autowired
    AMQPService amqpService;

    RestTemplate restTemplate = new RestTemplate();

    @Value("${author.service.url}")
    private String userUrl;

    private final String groupUrl = "";

    //Post
    @PostMapping("")
    public ResponseEntity<PostResponse> newPost(@RequestHeader(value = "x-user-id") Long userId,
                                        @RequestParam(value = "content", required = false) String content,
                                        @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                        @RequestParam("privacy") Integer privacy) {
        try {
            String Url= userUrl + "/users/info/" + userId.toString();
            NewPostMessage message = new NewPostMessage();
            if  ((content == null &&  images==null) || privacy < 0 || privacy > 3 ) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if  ((content == null && (images.get(0).isEmpty() || images.isEmpty() ))) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            if((content!=null && content.length()>5000) || (images!=null && images.size()>5)){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            User user = restTemplate.getForObject(Url, User.class, userId);
//            User user =new User(1L,"asdfzc",new User.profile(new Image("asdzcxv",Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));

            Post post = new Post();
            post.setContent(content);
            if(content==null)post.setContent("");
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
            if(privacy!=1){
                message.setTimestamp(new Timestamp(System.currentTimeMillis()));
                message.setAuthor(new AuthorDto().convertToAuthor(user));
                message.setType(NewPostMessage.Type.NEW);
                message.setId(savedPost.getId());
                amqpService.sendNotifyNewPostMessage(message);
            }
            PostResponse response = new PostResponse();
            response.convertToResponse(post);
            response.setImages(post.getImages());
            response.getAuthor().setId(user.getId());
            response.getAuthor().setName(user.getName());
            response.getAuthor().setAvatar(user.getAvatar());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Share Post
    @PostMapping("/share")
    public ResponseEntity<PostResponse> sharePost(@RequestHeader(value = "x-user-id") Long userId,
                                                  @RequestBody ShareModel model) {

        Post originPost = postRepository.findById(model.getPostId()).orElse(null);
        NewPostMessage message = new NewPostMessage();
        if(originPost == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        String Url1= userUrl + "/users/info/" + userId.toString();
        String Url2= userUrl + "/users/info/" + originPost.getAuthorId();
//        User user =new User(2L,"asdfzc",new User.profile(new Image("asdzcxv",Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));
//        User author =new User(1L,"asdfzc",new User.profile(new Image("asdzcxv",Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));
        User user = restTemplate.getForObject(Url2, User.class, userId);
        User author = restTemplate.getForObject(Url2, User.class, userId);
        message.setAuthor(new AuthorDto().convertToAuthor(user));
        message.setTimestamp(new Timestamp(System.currentTimeMillis()));
        message.setType(NewPostMessage.Type.SHARE);
        if(user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Post post = new Post();
        post.setOriginPost(originPost);
        post.setPrivacy(model.getPrivacy());
        post.setContent(model.getContent());
        post.setAuthorId(userId);
        if(model.getContent() == null) post.setContent("");

        OriginPostResponse originPostResponse = new OriginPostResponse();
        originPostResponse.setId(originPost.getId());
        originPostResponse.setAuthor(new AuthorDto().convertToAuthor(user));
        originPostResponse.setImages(originPost.getImages());
        originPostResponse.setCreatedAt(originPost.getCreatedAt());
        originPostResponse.setUpdatedAt(originPost.getUpdatedAt());

        originPost.getSharedPosts().add(post);
        postRepository.save(originPost);
        List<Post> posts=originPost.getSharedPosts();
        Post savedPost=posts.get(posts.size()-1);
        PostResponse response = new PostResponse();
        message.setId(savedPost.getId());
        amqpService.sendNotifyNewPostMessage(message);

        response.setOriginPost(originPostResponse);
        response.setId(savedPost.getId());
        response.setCreatedAt(savedPost.getCreatedAt());
        response.setContent(savedPost.getContent());
        response.setPrivacy(savedPost.getPrivacy());
        response.setAuthor(new AuthorDto().convertToAuthor(author));
        response.setUpdatedAt(savedPost.getUpdatedAt());
        return new ResponseEntity<>(response,HttpStatus.CREATED);

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

    @GetMapping("/timeline")
    public ResponseEntity<Page<PostResponse>> getTimeLinePost(@RequestHeader(value = "x-user-id",required = false) Long userId,
                                                              @RequestParam(defaultValue = "0") Integer page,
                                                              @RequestParam(defaultValue = "10") Integer limit,
                                                              @RequestParam(value = "profile") Long authorId){
        if(!authorId.equals(userId)){
            return new ResponseEntity<>(postService.getPostsByAuthorIdAndPublic(page,limit,authorId,userId),HttpStatus.OK);
        }
        return new ResponseEntity<>(postService.getPostsByAuthor(page,limit,authorId,userId),HttpStatus.OK);
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

        if(Objects.equals(postRepository.findById(postId).orElse(null).getAuthorId(), userId))
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

    // Group Post
    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Post>> groupPosts(@RequestParam("postIds") List<UUID> postIds){


        return new ResponseEntity<>(postService.getPostsByListId(postIds),HttpStatus.OK);
    }

    //Comment
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> comment(@RequestHeader(value = "x-user-id") Long userId,
                                           @PathVariable(value = "id") UUID postId,
                                           @RequestBody NewCommentModel commentModel){
        String SenderUrl= userUrl + "/users/info/" + userId.toString();

        NewCommentMessage message1=new NewCommentMessage();
        SocketCommentCountMessage message2= new SocketCommentCountMessage();
        SocketNewCommentMessage message3;

        if(commentModel.getContent()==null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        Post post = postService.findById(postId);
        String AuthorUrl= userUrl + "/users/info/" +post.getAuthorId();
        User user = restTemplate.getForObject(SenderUrl, User.class, userId);
        User author = restTemplate.getForObject(AuthorUrl, User.class, post.getAuthorId());
//        User user =new User(2L,"asdfzc",new User.profile(new Image("asdzcxv",Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));
//        User author =new User(2L,"asdfzc",new User.profile(new Image("asdzcxv",Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));

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
        SocketNewCommentMessage.CommentResponseForSocket socketComment = new SocketNewCommentMessage.CommentResponseForSocket(savedComment.getId(),savedComment.getContent(),new AuthorDto().convertToAuthor(user));

        message1.setCommentId(savedComment.getId());
        message1.setPostId(postId);
        message1.setAuthor(new AuthorDto().convertToAuthor(author));
        message1.setTimestamp(Date.from(Instant.now()));
        message1.setSender(new AuthorDto().convertToAuthor(user));

        message2.setCommentCount(post.getCommentCount());
        message2.setPostId(postId);
        message2.setTimestamp(Date.from(Instant.now()));

        message3 = new SocketNewCommentMessage(postId,socketComment,Date.from(Instant.now()));

        amqpService.sendNotifyNewCommentMessage(message1);
        amqpService.sendSocketCommentCountMessage(message2);
        amqpService.sendSocketNewCommentMessage(message3);

        System.out.println(message2.getCommentCount() + " "+ message3.getTimestamp());
        CommentResponse commentResponse = new CommentResponse();
        commentResponse.setId(savedComment.getId());
        commentResponse.getAuthor().setId(userId);
        commentResponse.getAuthor().setName(user.getName());
        commentResponse.getAuthor().setAvatar(user.getAvatar());
        commentResponse.setContent(savedComment.getContent());
        commentResponse.setCreatedAt(savedComment.getCreatedAt());
        commentResponse.setUpdatedAt(savedComment.getUpdatedAt());
        commentResponse.setParentId(savedComment.getParentId());
        commentResponse.setLevel(savedComment.getLevel());

        return new ResponseEntity<>(commentResponse, HttpStatus.CREATED);

    }

    @PostMapping("/{id}/comments/reply")
    public ResponseEntity<CommentResponse> replyComment(@RequestHeader(value = "x-user-id",required = false) Long userId,
                                                        @PathVariable(value = "id") UUID postId,
                                                        @RequestBody NewReplyModel replyModel){
        String Url= userUrl + "/users/info/" + userId.toString();
        Post post = postService.findById(postId);
        User user = restTemplate.getForObject(Url, User.class, userId);
//        User user =new User(2L,"asdfzc",new User.profile(new Image("asdzcxv",Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));
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

        int level = reply.getLevel();

        UUID parentId = reply.getParentId();
        while(level>0){
            Comment parent = commentRepository.findById(parentId).orElse(null);
            parent.setReplyCount(parent.getReplyCount()+1);
            commentRepository.save(parent);
            level=parent.getLevel();
            parentId=parent.getParentId();
        }

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

    @DeleteMapping("/{id}/comments")
    public ResponseEntity<String> deleteComment(@RequestHeader(value = "x-user-id",required = false) Long userId,
                                                 @PathVariable(value = "id") UUID commentId)
    {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if(comment == null){
            return new ResponseEntity<>("Không tìm thấy comment!",HttpStatus.NOT_FOUND);
        }


        if(!Objects.equals(userId, comment.getUserId())){
            return new ResponseEntity<>("Bạn không thể xóa comment này!",HttpStatus.FORBIDDEN);
        }

        if(!commentService.deleteCommentS(comment)){
            return new ResponseEntity<>("Xóa bình luận không thành công!", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Xóa bình luận thành công!",HttpStatus.ACCEPTED);


    }

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
                                                     @RequestBody NewReactionModel reactionModel )  {
        ReactionResponse response = new ReactionResponse();
        String senderUrl= userUrl + "/users/info/" + userId.toString();
        ReactionPostMessage message1 = new ReactionPostMessage();
        if(reactionModel.getEmoji()>5 || reactionModel.getEmoji()<0) {
            response.setMassage("Emoji nằm trong khỏang 0 đến 5");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        Post post = postService.findById(postId) ;
        if(post==null) {
            response.setMassage("Không tìm thấy bài viết " + postId.toString());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        String authorUrl= userUrl + "/users/info/" + userId.toString();
        User user = restTemplate.getForObject(senderUrl, User.class, userId);
        User author = restTemplate.getForObject(authorUrl, User.class, post.getAuthorId());
//        User user =new User(2L,"asdfzc",new User.profile(new Image("asdzcxv",Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));
//        User author =new User(2L,"asdfzc",new User.profile(new Image("asdzcxv",Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));

        message1 = new ReactionPostMessage(postId,new AuthorDto().convertToAuthor(author),new AuthorDto().convertToAuthor(user),reactionModel.getEmoji(),new Timestamp(System.currentTimeMillis()));
        SocketReactionPostMessage message2 = new SocketReactionPostMessage();

        amqpService.sendNotifyReactionMessage(message1);
        Reaction reaction=reactionRepository.getByPostIdAndUserId(postId,userId);
        if(reaction!=null){
            reaction.setEmoji(reactionModel.getEmoji());
            reactionRepository.save(reaction);
            response.setMassage("Đã cập nhật tương tác!");
            response.setReactionCount(post.getReactionCount());
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        message2 = new SocketReactionPostMessage(postId,reactionModel.getEmoji(),post.getReactionCount()+1,new Timestamp(System.currentTimeMillis()));
        amqpService.sendSocketReactionMessage(message2);
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
        if(reactionModel==null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
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
        String SenderUrl= userUrl + "/users/info/" + userId.toString();
        String AuthorUrl= userUrl + "/users/info/" +comment.getUserId();
        User user = restTemplate.getForObject(SenderUrl, User.class, userId);
        User author = restTemplate.getForObject(AuthorUrl, User.class, comment.getUserId());
//        User user =new User(2L,"asdfzc",new User.profile(new Image("asdzcxv",Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));
//        User author =new User(2L,"asdfzc",new User.profile(new Image("asdzcxv",Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG));

        CommentReactionMessage message= new CommentReactionMessage();
        message.setTimestamp(Date.from(Instant.now()));
        message.setEmoji(reactionModel.getEmoji());
        message.setCommentId(commentId);
        message.setPostId(comment.getPost().getId());
        message.setAuthor(new AuthorDto().convertToAuthor(author));
        message.setCommentator(new AuthorDto().convertToAuthor(user));

        amqpService.sendNotifyCommentReactionMessage(message);


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

    @Transactional
    @DeleteMapping("/{id}/commentReaction")
    public ResponseEntity<ReactionResponse> deteleCommentReaction(@RequestHeader(value = "x-user-id") Long userId,
                                                           @PathVariable("id") UUID commentId){
        CommentReaction reaction=commentReactionRepository.getByCommentIdAndUserId(commentId,userId);
        ReactionResponse response= new ReactionResponse();
        if(reaction == null){
            response.setMassage("Bạn chưa tương tác bài viết này!");
            return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
        }
        if(!reaction.getUserId().equals(userId)){
            response.setMassage("Bạn không thể xóa tương tác này");
            return new ResponseEntity<>(response,HttpStatus.FORBIDDEN);
        }
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if(comment==null) {
            response.setMassage("Không tìm thấy bài viết " + commentId.toString());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        reactionRepository.deleteReactionByPostIdAndUserId(commentId,userId);
        commentRepository.save(comment);
        response.setMassage("Xóa tương tác thành công!");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

//    @GetMapping("/group/{groupId}")
//    public ResponseEntity<Page<PostResponse>> getGroupPost(@RequestHeader@PathVariable("groupId") UUID groupId) {
//
//
//
//        return new ResponseEntity<>(posts, HttpStatus.OK);
//    }

//    @PostMapping("/groupPost")
//    public ResponseEntity<PostResponse> newGroupPost(@RequestHeader(value = "x-user-id") Long userId,
//                                                     @RequestParam(value = "content", required = false) String content,
//                                                     @RequestParam(value = "images", required = false) List<MultipartFile> images,
//                                                     @RequestParam("privacy") Integer privacy,
//                                                     @RequestParam(value = "groupId") UUID groupId){
//
//    }

    @PostMapping("/test")
    public ResponseEntity<NewPostMessage> test(@RequestHeader(value = "x-user-id") Long userId){
        NewPostMessage message = new NewPostMessage();

        ReactionPostMessage message1 = new ReactionPostMessage();
        message1.setPostId(UUID.randomUUID());
        message1.setSender(new AuthorDto().convertToAuthor(new User(2L,"asdfzc",new User.profile(new Image("asdzcxv",Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG))));
        message1.setEmoji(1);
        message1.setAuthor(new AuthorDto().convertToAuthor(new User(2L,"asdfzc",new User.profile(new Image("asdzcxv",Format.JPG, Date.from(Instant.now()),Date.from(Instant.now())),"asdfz",Format.JPG))));
        message.setType(NewPostMessage.Type.NEW);

        amqpService.sendNotifyReactionMessage(message1);

        return new ResponseEntity<>(message,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> GetById(@RequestHeader(value = "x-user-id") Long userId,
                                                @PathVariable("id") UUID postId){
        Post post = postRepository.findById(postId).orElse(null);
        if(post==null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        PostResponse response = new PostResponse();
        response.convertToResponse(post);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}