package trying.cosmos.domain.planet.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import trying.cosmos.domain.notification.entity.NotificationTarget;
import trying.cosmos.domain.notification.service.NotificationService;
import trying.cosmos.domain.planet.entity.Planet;
import trying.cosmos.domain.planet.repository.PlanetRepository;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.DateUtils;
import trying.cosmos.global.utils.push.PushUtils;
import trying.cosmos.global.utils.push.dto.PushRequest;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PlanetService {

    private final UserRepository userRepository;
    private final PlanetRepository planetRepository;
    private final NotificationService notificationService;
    private final PushUtils pushUtils;
    private final MessageSourceAccessor messageSource;

    @Transactional
    public Planet create(Long userId, String name, String type) {
        User user = userRepository.findById(userId).orElseThrow();
        if (user.getPlanet() != null) {
            throw new CustomException(ExceptionType.PLANET_CREATE_FAILED);
        }
        return planetRepository.save(new Planet(userRepository.findById(userId).orElseThrow(), name, type, generateCode()));
    }

    private String generateCode() {
        String code = RandomStringUtils.random(6, true, true);
        while (planetRepository.existsByInviteCode(code)) {
            code = RandomStringUtils.random(6, true, true);
        }
        return code;
    }

    @Transactional
    public void join(Long userId, String code) {
        Planet planet = planetRepository.searchByInviteCode(code).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();
        planet.join(user);
        pushUtils.pushTo(
                user.getMate(),
                messageSource.getMessage("notification.planet.join.title"),
                messageSource.getMessage("notification.planet.join.body"),
                new PushRequest.Data(null, NotificationTarget.JOIN.toString(), null)
        );
    }

    public Planet find(String inviteCode) {
        Planet planet = planetRepository.searchByInviteCode(inviteCode).orElseThrow();
        if (planet.isFull()) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        return planet;
    }

    @Transactional
    public Planet update(Long userId, String name, LocalDate date, String image) {
        User user = userRepository.findById(userId).orElseThrow();
        Planet planet = user.getPlanet();
        if (planet == null) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        planet.update(name, date, image);
        return planet;
    }

    @Transactional
    public void leave(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        Planet planet = user.getPlanet();
        if (planet == null) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        pushUtils.pushTo(
                user.getMate(),
                messageSource.getMessage("notification.planet.leave.title"),
                messageSource.getMessage("notification.planet.leave.body", new String[]{DateUtils.getFormattedDate(LocalDate.now(), "yyyy년 MM월 dd일"), planet.getName()}),
                new PushRequest.Data(null, NotificationTarget.LEAVE.toString(), null)
        );
        planet.leave(user);
        notificationService.deleteAll(user.getId());
    }
}
