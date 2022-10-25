package trying.cosmos.domain.planet.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import trying.cosmos.domain.common.DateAuditingEntity;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.global.aop.LogSpace;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static trying.cosmos.global.exception.ExceptionType.NO_DATA;
import static trying.cosmos.global.exception.ExceptionType.NO_PERMISSION;

@Slf4j
@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Planet extends DateAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planet_id")
    private Long id;

    @ToString.Exclude
    @OneToMany(mappedBy = "planet")
    private List<User> owners = new ArrayList<>();

    private String name;

    private LocalDate meetDate;

    @ToString.Exclude
    @Enumerated(EnumType.STRING)
    private PlanetImageType image;

    private String inviteCode;

    private boolean isDeleted;

    // Constructor
    public Planet(User user, String name, PlanetImageType image, String code) {
        this.name = name;
        this.inviteCode = code;
        this.image = image;
        this.meetDate = LocalDate.now();
        this.isDeleted = false;
        user.setPlanet(this);
        this.owners.add(user);
    }

    // Convenience Method
    public void join(User guest) {
        if (owners.size() != 1) {
            throw new CustomException(ExceptionType.PLANET_JOIN_FAILED);
        }
        User owner = owners.get(0);
        if (owner.equals(guest)) {
            throw new CustomException(ExceptionType.PLANET_JOIN_FAILED);
        }
        this.owners.add(guest);
        owner.setMate(guest);
        guest.setMate(owner);
        guest.setPlanet(this);
    }

    public void update(String name, int days) {
        this.name = name;
        this.meetDate = LocalDate.now().minusDays(days - 1);
    }

    public int getDday() {
        return (int) Duration.between(this.meetDate.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays() + 1;
    }

    public boolean isOwnedBy(User user) {
        return owners.contains(user);
    }

    public String getInviteCode(Long userId) {
        if (owners.size() != 1) {
            // 초대코드가 존재하지 않음
            throw new CustomException(NO_DATA);
        }
        if (!owners.get(0).getId().equals(userId)) {
            throw new CustomException(NO_PERMISSION);
        }
        return this.inviteCode;
    }

    public boolean isFull() {
        return this.owners.size() > 1;
    }

    public void leave(User user) {
        if (!owners.contains(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }
        this.owners.remove(user);
        user.setPlanet(null);
        if (this.owners.isEmpty()) {
            log.info("{}Delete Planet {}", LogSpace.getSpace(),id);
            this.isDeleted = true;
        }
    }
}
