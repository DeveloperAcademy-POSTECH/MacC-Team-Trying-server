package trying.cosmos.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import trying.cosmos.domain.course.service.CourseService;
import trying.cosmos.domain.user.dto.request.*;
import trying.cosmos.domain.user.dto.response.UserEmailExistResponse;
import trying.cosmos.domain.user.dto.response.UserFindResponse;
import trying.cosmos.domain.user.dto.response.UserLoginResponse;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.auth.AuthKey;
import trying.cosmos.global.auth.AuthorityOf;

import static trying.cosmos.global.auth.Authority.USER;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CourseService courseService;

    @GetMapping("/exist")
    public UserEmailExistResponse isEmailExist(@RequestParam String email) {
        return new UserEmailExistResponse(userService.isExist(email));
    }

    @PostMapping
    public void join(@RequestBody @Validated UserJoinRequest request) {
        userService.join(request.getEmail(), request.getPassword(), request.getName());
    }

    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody @Validated UserLoginRequest request) {
        return new UserLoginResponse(userService.login(request.getEmail(), request.getPassword(), request.getDeviceToken()));
    }

    @AuthorityOf(USER)
    @GetMapping("/me")
    public UserFindResponse findMe() {
        return new UserFindResponse(userService.find(AuthKey.getKey()));
    }

    @PatchMapping("/password")
    public void resetPassword(@RequestBody @Validated UserResetPasswordRequest request) {
        userService.resetPassword(request.getEmail());
    }

    @AuthorityOf(USER)
    @PutMapping("/name")
    public void updateName(@RequestBody @Validated UserUpdateNameRequest request) {
        userService.updateName(AuthKey.getKey(), request.getName());
    }

    @AuthorityOf(USER)
    @PutMapping("/password")
    public void updatePassword(@RequestBody @Validated UserUpdatePasswordRequest request) {
        userService.updatePassword(AuthKey.getKey(), request.getPassword());
    }

    @AuthorityOf(USER)
    @DeleteMapping("/logout")
    public void logout() {
        userService.logout(AuthKey.getKey());
    }

    @AuthorityOf(USER)
    @DeleteMapping
    public void withdraw() {
        userService.withdraw(AuthKey.getKey());
    }
}
