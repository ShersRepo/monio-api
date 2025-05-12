package com.smart_tiger.monio.modules.user;

import com.smart_tiger.monio.middleware.response.ApiResponse;
import com.smart_tiger.monio.modules.user.dto.UserAccountCreateDto;
import com.smart_tiger.monio.modules.user.dto.UserAccountDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.smart_tiger.monio.middleware.response.SuccessResponseType.okFetch;
import static com.smart_tiger.monio.middleware.response.SuccessResponseType.okWithoutDataResponse;
import static java.net.URI.create;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserAccountController {

    private final UserAccountService userAccountService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createUserAccount(@Valid @RequestBody UserAccountCreateDto userAccount) {
        String entityId = userAccountService.createUserAccount(userAccount).toString();
        return created(create("user/" + entityId))
                .body(okWithoutDataResponse("User Account setup"));
    }

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserAccountDto>> getUserAccount(@PathVariable String username) {
        return status(OK)
                .body(okFetch(userAccountService.getUserAccount(username)));
    }

}
