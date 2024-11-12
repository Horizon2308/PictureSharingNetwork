package hung.haduc.picturesharingnetwork.controllers;

import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.http_responses.ErrorResponse;
import hung.haduc.picturesharingnetwork.http_responses.SuccessResponse;
import hung.haduc.picturesharingnetwork.services.FollowService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/follows")
public class FollowController {

    private final FollowService followService;
    private final Logger logger = LoggerFactory.getLogger(FollowController.class);

    @GetMapping("/{id}")
    public ResponseEntity<?> follow(@PathVariable Long id) {
        try {
            followService.followByUserId(id);
            logger.info("Followed user by id: " + id + " successfully !");
            return ResponseEntity.ok(new SuccessResponse<>("Followed successfully !"));
        } catch (DataNotFoundException e) {
            logger.info("Followed user by id: " + id + " error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/unfollow/{id}")
    public ResponseEntity<?> unfollow(@PathVariable Long id) {
        try {
            followService.unFollowByUserId(id);
            logger.info("Unfollowed user by id: " + id + " successfully !");
            return ResponseEntity.ok(new SuccessResponse<>("Unfollowed successfully !"));
        } catch (DataNotFoundException e) {
            logger.info("Unfollowed user by id: " + id + " error: " + e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

}
