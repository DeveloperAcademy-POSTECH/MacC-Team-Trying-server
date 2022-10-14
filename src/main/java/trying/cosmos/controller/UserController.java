package trying.cosmos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.auth.AuthKey;
import trying.cosmos.auth.AuthorityOf;
import trying.cosmos.controller.request.*;
import trying.cosmos.controller.response.UserFindResponse;
import trying.cosmos.controller.response.UserLoginResponse;
import trying.cosmos.service.UserService;

import static trying.cosmos.entity.component.Authority.USER;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/validate-email")
    public void validateEmail(@RequestBody @Validated UserValidateEmailRequest request) {
        userService.validateEmail(request.getEmail());
    }

    @PostMapping
    public void join(@RequestBody @Validated UserJoinRequest request) {
        userService.join(request.getEmail(), request.getPassword());
    }

    @PostMapping("/certificate")
    public void certificate(@RequestBody @Validated UserCertificationRequest request) {
        userService.certificate(request.getEmail(), request.getCode());
    }

    @PostMapping("/{email}")
    public void createUser(@PathVariable String email, @RequestBody @Validated UserCreateControllerRequest request) {
        userService.create(email, request.getName());
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody @Validated UserLoginRequest request) {
        return new UserLoginResponse(userService.login(request.getEmail(), request.getPassword(), request.getDeviceToken()));
    }

    @PatchMapping("/password")
    public void resetPassword(@RequestBody @Validated UserResetPasswordRequest request) {
        userService.resetPassword(request.getEmail());
    }

    @AuthorityOf(USER)
    @GetMapping("/me")
    public UserFindResponse findMe() {
        return new UserFindResponse(userService.find(AuthKey.get()));
    }

    @AuthorityOf(USER)
    @PutMapping
    public void update(@RequestBody @Validated UserUpdateControllerRequest request) {
        userService.update(AuthKey.get(), request.getName(), request.getPassword());
    }

    @AuthorityOf(USER)
    @DeleteMapping("/logout")
    public void logout() {
        userService.logout(AuthKey.get());
    }

    @AuthorityOf(USER)
    @DeleteMapping
    public void withdraw() {
        userService.withdraw(AuthKey.get());
    }
}
