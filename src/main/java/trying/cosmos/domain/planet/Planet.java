package trying.cosmos.domain.planet;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.domain.common.DateAuditingEntity;
import trying.cosmos.domain.user.User;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Planet extends DateAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planet_id")
    private Long id;

    @OneToMany(mappedBy = "planet")
    private List<User> owners = new ArrayList<>();

    private String name;

    private LocalDate meetDate;

    @Enumerated(EnumType.STRING)
    private PlanetImageType image;

    private String inviteCode;

    // Constructor
    public Planet(User user, String name, PlanetImageType image) {
        setPlanet(user, this);
        this.name = name;
        this.inviteCode = UUID.randomUUID().toString();
        this.image = image;
        this.meetDate = LocalDate.now();
    }

    // Convenience Method
    public void join(User user) {
        if (this.owners.size() >= 2 || this.owners.contains(user)) {
            throw new CustomException(ExceptionType.PLANET_JOIN_FAILED);
        }
        setPlanet(user, this);
    }

    private static void setPlanet(User user, Planet planet) {
        user.setPlanet(planet);
        planet.owners.add(user);
    }

    public List<String> getOwnersName() {
        return this.owners.stream().map(User::getName).collect(Collectors.toList());
    }

    public void updateDday(int days) {
        this.meetDate = LocalDate.now().minusDays(days);
    }

    public int getDday() {
        return (int) Duration.between(this.meetDate.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays() + 1;
    }

    public User getMate(User me) {
        if (!this.owners.contains(me)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }
        if (this.owners.size() == 1) {
            return null;
        } else {
            ArrayList<User> users = new ArrayList<>(this.owners);
            users.remove(me);
            return users.get(0);
        }
    }

    public void authorize(Long userId) {
        for (User owner : owners) {
            if (owner.getId().equals(userId)) {
                return;
            }
        }
        throw new CustomException(ExceptionType.NO_PERMISSION);
    }

    public boolean beOwnedBy(User user) {
        return owners.contains(user);
    }
}