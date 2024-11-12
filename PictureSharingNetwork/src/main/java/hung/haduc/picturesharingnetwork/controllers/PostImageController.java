package hung.haduc.picturesharingnetwork.controllers;

import hung.haduc.picturesharingnetwork.http_responses.ErrorResponse;
import hung.haduc.picturesharingnetwork.http_responses.SuccessResponse;
import hung.haduc.picturesharingnetwork.services.PostImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/images")
public class PostImageController {

    private final PostImageService postImageService;

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@PathVariable Long id,
                                          @ModelAttribute("listOfFiles") ArrayList<MultipartFile> listOfFiles) {
        try {
            postImageService.saveAllImages(id, listOfFiles);
            return ResponseEntity.ok(new SuccessResponse<>("Upload images successfully !"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }



}
