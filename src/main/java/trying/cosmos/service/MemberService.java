package trying.cosmos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.auth.TokenProvider;
import trying.cosmos.entity.Member;
import trying.cosmos.repository.MemberRepository;
import trying.cosmos.service.request.MemberJoinRequest;
import trying.cosmos.service.request.MemberLoginRequest;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public Member join(MemberJoinRequest request) {
        return memberRepository.save(
                Member.builder()
                        .email(request.getEmail())
                        .password(request.getPassword())
                        .name(request.getName())
                        .build()
        );
    }

    public String login(MemberLoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow();
        member.login();
        return tokenProvider.getAccessToken(member);
    }
}
