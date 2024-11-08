package hung.haduc.picturesharingnetwork.controllers;

import hung.haduc.picturesharingnetwork.dtos.PostDTO;
import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.http_responses.ErrorResponse;
import hung.haduc.picturesharingnetwork.http_responses.SuccessResponse;
import hung.haduc.picturesharingnetwork.services.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/posts")
public class PostController {

    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<?> getAllPostFollowed (
             @RequestParam(defaultValue = "1", value = "sort_option") int sortOption,
             @RequestParam(defaultValue = "", value = "keyword") String keyword,
             @RequestParam(defaultValue = "0") Integer page,
             @RequestParam(defaultValue = "10") Integer limit) {
        try {
            return ResponseEntity.ok()
                    .body(new SuccessResponse<>(postService
                            .getAllPostsFollowed(sortOption, keyword, page, limit)
                            , "Get all posts followed successfully !"));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1", value = "sort_option") int sortOption,
            @RequestParam(defaultValue = "", value = "keyword") String keyword,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            postService.getAllPostsByUserId(sortOption, keyword, userId, page, limit);
            return ResponseEntity.ok()
                    .body(new SuccessResponse<>(postService
                            .getAllPostsFollowed(sortOption, keyword, page, limit)
                            , "Get all posts by user id successfully !"));
        } catch (DataNotFoundException e) {
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
            return ResponseEntity.badRequest().body(new ErrorResponse<>(errorMessages));
        }
        try {
            postService.createPost(postDTO);
            return ResponseEntity.ok(new SuccessResponse<>("Created post successfully !"));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }
}
