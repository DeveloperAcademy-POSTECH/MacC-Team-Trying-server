package trying.cosmos.domain.planet.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.global.aop.LogSpace;
import trying.cosmos.global.auditing.DateAuditingEntity;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    private String image;

    private String inviteCode;

    private boolean isDeleted;

    // Constructor
    public Planet(User user, String name, String image, String inviteCode) {
        this.name = name;
        this.inviteCode = inviteCode;
        this.image = image;
        this.meetDate = LocalDate.now();
        this.isDeleted = false;
        user.setPlanet(this);
        this.owners.add(user);
    }

    // Convenience Method
    public void join(User mate) {
        if (owners.size() != 1) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        User owner = owners.get(0);
        if (owner.equals(mate)) {
            throw new CustomException(ExceptionType.PLANET_JOIN_FAILED);
        }
        this.owners.add(mate);
        owner.setMate(mate);
        mate.setMate(owner);
        mate.setPlanet(this);
    }

    public void update(String name, LocalDate date, String image) {
        if (date.isAfter(LocalDate.now())) {
            throw new CustomException(ExceptionType.PLANET_UPDATE_FAILED);
        }
        this.meetDate = date;
        this.name = name;
        this.image = image;
    }

    public int getDday() {
        return (int) Duration.between(this.meetDate.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays() + 1;
    }

    public boolean isFull() {
        return this.owners.size() > 1;
    }

    public void leave(User user) {
        if (!owners.contains(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }

        User mate = user.getMate();
        if (mate != null) {
            mate.setMate(null);
        }
        user.setMate(null);
        this.owners.remove(user);
        user.setPlanet(null);

        if (this.owners.isEmpty()) {
            log.info("{}Delete Planet {}", LogSpace.getSpace(), id);
            this.isDeleted = true;
        }
    }

    public boolean isOwnedBy(User user) {
        return this.owners.contains(user);
    }
}
