package hung.haduc.picturesharingnetwork.controllers;

import hung.haduc.picturesharingnetwork.dtos.AuthenticationDTO;
import hung.haduc.picturesharingnetwork.http_responses.ErrorResponse;
import hung.haduc.picturesharingnetwork.http_responses.SuccessResponse;
import hung.haduc.picturesharingnetwork.services.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @RequestMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthenticationDTO authenticationDTO,
                                      BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result. getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(new ErrorResponse<>(errorMessages));
        }
        try {
            authenticationService.register(authenticationDTO);
            return ResponseEntity.ok().body(new SuccessResponse<>("Register successfully !"));
        } catch (MessagingException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

}
