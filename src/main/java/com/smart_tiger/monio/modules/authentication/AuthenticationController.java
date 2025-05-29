package com.smart_tiger.monio.modules.authentication;

import com.smart_tiger.monio.middleware.response.ApiResponse;
import com.smart_tiger.monio.modules.user.dto.UserAccountDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> login(
            @Valid @RequestBody AuthenticationDetailsDto loginRequest,
            HttpServletResponse response
    ) {
        try {
            return ok().body(authenticationService.authenticateRequestUser(response, loginRequest));
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(UNAUTHORIZED)
                    .body(
                            ApiResponse.<Void> builder()
                            .message("Invalid username or password")
                            .status(UNAUTHORIZED.value())
                            .data(null)
                            .build()
                    );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
        return ok(authenticationService.logoutUser(request, response));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<UserAccountDto>> refreshUser(HttpServletRequest request) {
        return ok(authenticationService.refreshUser(request));
    }

}
