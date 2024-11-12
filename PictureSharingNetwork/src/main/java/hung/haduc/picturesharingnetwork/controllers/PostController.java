package hung.haduc.picturesharingnetwork.controllers;

import hung.haduc.picturesharingnetwork.dtos.PostDTO;
import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.http_responses.ErrorResponse;
import hung.haduc.picturesharingnetwork.http_responses.SuccessResponse;
import hung.haduc.picturesharingnetwork.responses.PostListResponse;
import hung.haduc.picturesharingnetwork.services.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/posts")
public class PostController {

    private final PostService postService;
    private final Logger logger = LoggerFactory.getLogger(PostController.class);

    @GetMapping("")
    public ResponseEntity<?> getAllPostFollowed (
             @RequestParam(defaultValue = "1", value = "sort_option") int sortOption,
             @RequestParam(defaultValue = "", value = "keyword") String keyword,
             @RequestParam(defaultValue = "0") Integer page,
             @RequestParam(defaultValue = "10") Integer limit) {
        try {
            var posts = postService
                    .getAllPostsFollowed(sortOption, keyword, page, limit);
            int totalPage = posts.getTotalPages();
            PostListResponse postListResponse = PostListResponse.builder()
                    .posts(posts)
                    .totalPage(totalPage)
                    .build();
            logger.info("Got all post followed successfully !");
            return ResponseEntity.ok()
                    .body(new SuccessResponse<>(postListResponse,
                            "Got all posts followed successfully !"));
        } catch (DataNotFoundException e) {
            logger.info("Got all post followed error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1", value = "sort_option") int sortOption,
            @RequestParam(defaultValue = "", value = "keyword") String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            var posts = postService
                    .getAllPostsByUserId(sortOption, keyword, userId, page, limit);
            int totalPage = posts.getTotalPages();
            PostListResponse postListResponse = PostListResponse.builder()
                    .posts(posts)
                    .totalPage(totalPage)
                    .build();
            logger.info("Got all post by user id: " + userId + " successfully !");
            return ResponseEntity.ok()
                    .body(new SuccessResponse<>(postListResponse
                            , "Got all posts by user id successfully !"));
        } catch (DataNotFoundException e) {
            logger.info("Got all post by user id: " + userId + ", error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @PostMapping("")
    public ResponseEntity<?> createPost(@Valid @RequestBody PostDTO postDTO,
                                        BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            logger.info("Created post error: " + errorMessages);
            return ResponseEntity.badRequest().body(new ErrorResponse<>(errorMessages));
        }
        try {
            var newPost = postService.createPost(postDTO);
            logger.info("Created post: " + newPost.toString() + " successfully !");
            return ResponseEntity.ok(new SuccessResponse<>(newPost
                    , "Created post successfully !"));
        } catch (DataNotFoundException e) {
            logger.info("Created post error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                        @Valid @RequestBody PostDTO postDTO,
                                        BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            logger.info("Updated post with id: " + id + " error: " + errorMessages);
            return ResponseEntity.badRequest().body(new ErrorResponse<>(errorMessages));
        }
        try {
            postService.updatePost(id, postDTO);
            logger.info("Updated post: " + id + " successfully !");
            return ResponseEntity.ok(new SuccessResponse<>("Updated post successfully !"));
        } catch (DataNotFoundException e) {
            logger.info("Updated post with id: " + id + " error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        try {
            postService.deletePost(id);
            logger.info("Deleted post: " + id + " successfully !");
            return ResponseEntity.ok(new SuccessResponse<>("Deleted post successfully !"));
        } catch (DataNotFoundException e) {
            logger.info("Deleted post with id: " + id + " error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPostDetailsById(@PathVariable Long id) {
        try {
            var postDetails = postService.getPostDetails(id);
            logger.info("Got post details with id: " + id + "successfully !");
            return ResponseEntity.ok(new SuccessResponse<>(postDetails
                    , "Got post details successfully !"));
        } catch (DataNotFoundException e) {
            logger.info("Got post details with id: " + id + " error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }
}
