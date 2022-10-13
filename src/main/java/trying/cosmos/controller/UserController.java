package trying.cosmos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.auth.AuthKey;
import trying.cosmos.auth.AuthorityOf;
import trying.cosmos.controller.request.UserCreateControllerRequest;
import trying.cosmos.controller.request.UserUpdateControllerRequest;
import trying.cosmos.controller.response.UserFindResponse;
import trying.cosmos.controller.response.UserLoginResponse;
import trying.cosmos.entity.component.Authority;
import trying.cosmos.service.UserService;
import trying.cosmos.service.request.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/validate-email")
    public void validateEmail(@RequestBody @Validated UserValidateEmailRequest request) {
        userService.validateEmail(request);
    }

    @PostMapping
    public void join(@RequestBody @Validated UserJoinRequest request) {
        userService.join(request);
    }

    @PostMapping("/certificate")
    public void certificate(@RequestBody @Validated UserCertificationRequest request) {
        userService.certificate(request);
    }

    @PostMapping("/{email}")
    public void createUser(@PathVariable String email, @RequestBody @Validated UserCreateControllerRequest request) {
        userService.create(new UserCreateServiceRequest(email, request));
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody @Validated UserLoginRequest request) {
        return new UserLoginResponse(userService.login(request));
    }

    @PatchMapping("/password")
    public void resetPassword(@RequestBody @Validated UserResetPasswordRequest request) {
        userService.resetPassword(request);
    }

    @GetMapping("/{name}")
    public UserFindResponse find(@PathVariable String name) {
        return new UserFindResponse(userService.find(name));
    }

    @AuthorityOf(Authority.USER)
    @PutMapping
    public void update(@RequestBody @Validated UserUpdateControllerRequest request) {
        userService.update(new UserUpdateServiceRequest(AuthKey.get(), request));
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping("/logout")
    public void logout() {
        userService.logout(AuthKey.get());
    }

    @AuthorityOf(Authority.USER)
    @DeleteMapping
    public void withdraw() {
        userService.withdraw(AuthKey.get());
    }
}
