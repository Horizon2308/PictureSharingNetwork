package hung.haduc.picturesharingnetwork.controllers;

import hung.haduc.picturesharingnetwork.dtos.CommentDTO;
import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.http_responses.ErrorResponse;
import hung.haduc.picturesharingnetwork.http_responses.SuccessResponse;
import hung.haduc.picturesharingnetwork.responses.CommentListResponse;
import hung.haduc.picturesharingnetwork.services.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final Logger logger = Logger.getLogger(getClass().getName());

    @GetMapping("/{post_id}")
    public ResponseEntity<?> getAllCommentsByPostId(
            @PathVariable(value = "post_id") Long postId,
            @RequestParam(defaultValue = "1", value = "sort_option") int sortOption,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer limit) {
        try {
            var comments = commentService.getAllCommentsByPostId(postId, sortOption, page, limit);
            int totalPage = comments.getTotalPages();
            CommentListResponse commentListResponse = CommentListResponse.builder()
                    .commentsList(comments)
                    .totalPage(totalPage)
                    .build();
            logger.info("Get all comments by id: " + postId + " successfully !");
            return ResponseEntity.ok()
                    .body(new SuccessResponse<>(commentListResponse
                            , "Get all comment by id successfully !"));
        } catch (DataNotFoundException e) {
            logger.info("Get all comment by id: " + postId + ", error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> createComment(@Valid @RequestBody CommentDTO commentDTO,
                                           BindingResult result, @PathVariable Long id) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            logger.info("Created comment error: " + errorMessages);
            return ResponseEntity.badRequest().body(new ErrorResponse<>(errorMessages));
        }
        try {
            var newComment = commentService.addComment(id, commentDTO);
            logger.info("Created comment: " + newComment.toString() + " successfully !");
            return ResponseEntity.ok()
                    .body(new SuccessResponse<>(newComment
                            , "Created comment successfully !"));
        } catch (DataNotFoundException e) {
            logger.info("Created comment with post id: " + id + ", error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id,
                                           @Valid @RequestBody CommentDTO commentDTO,
                                           BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            logger.info("Updated comment error: " + errorMessages);
            return ResponseEntity.badRequest().body(new ErrorResponse<>(errorMessages));
        }
        try {
            var updatedComment = commentService.updateComment(id, commentDTO);
            logger.info("Updated comment: " + updatedComment.toString() + " successfully !");
            return ResponseEntity.ok()
                    .body(new SuccessResponse<>(updatedComment
                            , "Updated comment successfully !"));
        } catch (Exception e) {
            logger.info("Updated comment with id: " + id + ", error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            logger.info("Deleted comment with id: " + id + " successfully !");
            return ResponseEntity.ok()
                    .body(new SuccessResponse<>("Deleted comment successfully !"));
        } catch (Exception e) {
            logger.info("Deleted comment with id: " + id + ", error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }
}
