package trying.cosmos.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.auth.TokenProvider;
import trying.cosmos.entity.Member;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;
import trying.cosmos.repository.MemberRepository;
import trying.cosmos.service.request.MemberJoinRequest;
import trying.cosmos.service.request.MemberLoginRequest;
import trying.cosmos.utils.BCryptUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public Member join(MemberJoinRequest request) {
        return memberRepository.save(
                Member.builder()
                        .email(request.getEmail())
                        .password(BCryptUtils.encrypt(request.getPassword()))
                        .name(request.getName())
                        .build()
        );
    }

    @Transactional
    public String login(MemberLoginRequest request) {
        Member member = memberRepository.findByEmail(request.getEmail()).orElseThrow();
        checkPassword(request, member);
        member.login(request.getDeviceToken());
        return tokenProvider.getAccessToken(member);
    }

    private static void checkPassword(MemberLoginRequest request, Member member) {
        if (!BCryptUtils.isMatch(request.getPassword(), member.getPassword())) {
            throw new CustomException(ExceptionType.INVALID_PASSWORD);
        }
    }
}
