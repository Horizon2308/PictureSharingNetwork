package hung.haduc.picturesharingnetwork.controllers;

import hung.haduc.picturesharingnetwork.dtos.AuthenticationDTO;
import hung.haduc.picturesharingnetwork.dtos.UserUpdateDTO;
import hung.haduc.picturesharingnetwork.exceptions.DataNotFoundException;
import hung.haduc.picturesharingnetwork.http_responses.ErrorResponse;
import hung.haduc.picturesharingnetwork.http_responses.SuccessResponse;
import hung.haduc.picturesharingnetwork.models.Token;
import hung.haduc.picturesharingnetwork.models.User;
import hung.haduc.picturesharingnetwork.requests.ChangePasswordRequest;
import hung.haduc.picturesharingnetwork.requests.ForgotPasswordRequest;
import hung.haduc.picturesharingnetwork.requests.LoginRequest;
import hung.haduc.picturesharingnetwork.responses.LoginResponse;
import hung.haduc.picturesharingnetwork.services.AuthenticationService;
import hung.haduc.picturesharingnetwork.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthenticationDTO authenticationDTO,
                                      BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(new ErrorResponse<>(errorMessages));
        }
        try {
            authenticationService.register(authenticationDTO);
            return ResponseEntity.ok().body(new SuccessResponse<>("Register successfully !"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
                                   BindingResult result,
                                   HttpServletRequest httpServletRequest) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(new ErrorResponse<>(errorMessages));
        }
        String userAgent = httpServletRequest.getHeader("User-Agent");
        try {
            String generatedToken = authenticationService.login(loginRequest);
            User user = authenticationService.getUserDetailFromToken(generatedToken);
            Token token = tokenService.addToken(user, generatedToken, this.isMobileUser(userAgent));
            LoginResponse loginResponse = LoginResponse.builder()
                    .token(token.getToken())
                    .refreshToken(token.getRefreshToken())
                    .tokenType(token.getTokenType())
                    .email(user.getEmail())
                    .userId(token.getId())
                    .build();
            return ResponseEntity.ok().body(new SuccessResponse<>(loginResponse,
                    "Login successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/activate-account")
    public ResponseEntity<?> activateAccount(@RequestParam(value = "activate_code")
                                             String activateCode) {
        try {
            authenticationService.activateAccount(activateCode);
            return ResponseEntity.ok().body(new SuccessResponse<>("Activate account successfully !"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest
                                                        forgotPasswordRequest,
                                            BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(new ErrorResponse<>(errorMessages));
        }
        try {
            authenticationService.forgotPassword(forgotPasswordRequest);
            return ResponseEntity.ok().body(new SuccessResponse<>("Your new password's account has been" +
                    "sent to your email. Please check your email !"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid
                                            @RequestBody ChangePasswordRequest changePasswordRequest,
                                            BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorMessages = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.badRequest().body(new ErrorResponse<>(errorMessages));
        }
        try {
            authenticationService.changePassword(changePasswordRequest);
            return ResponseEntity.ok().body(new SuccessResponse<>("Change password successfully !"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @PutMapping("/update-account")
    public ResponseEntity<?> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            return ResponseEntity.ok().body(new SuccessResponse<>(authenticationService.updateUser(userUpdateDTO),
                    "Updated account successfully !"));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }

    @GetMapping("/locking-user")
    public ResponseEntity<?> lockingUser() {
        try {
            authenticationService.lockingAccount();
            return ResponseEntity.ok().body(new SuccessResponse<>("Locked account successfully !"));
        } catch (DataNotFoundException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<>(e.getMessage()));
        }
    }


    private boolean isMobileUser(String userAgent) {
        return userAgent.toLowerCase().contains("mobile");
    }

}
