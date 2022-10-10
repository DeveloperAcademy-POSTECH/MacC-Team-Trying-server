package trying.cosmos.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import trying.cosmos.controller.response.MemberLoginResponse;
import trying.cosmos.service.MemberService;
import trying.cosmos.service.request.MemberJoinRequest;
import trying.cosmos.service.request.MemberLoginRequest;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public void join(@RequestBody MemberJoinRequest request) {
        memberService.join(request);
    }

    @PostMapping("/login")
    public MemberLoginResponse login(@RequestBody MemberLoginRequest request) {
        return new MemberLoginResponse(memberService.login(request));
    }
}
