package trying.cosmos.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.entity.component.DateEntity;
import trying.cosmos.entity.component.PlanetImageType;
import trying.cosmos.exception.CustomException;
import trying.cosmos.exception.ExceptionType;

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
public class Planet extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planet_id")
    private Long id;

    @OneToMany(mappedBy = "planet")
    private List<User> owners = new ArrayList<>();

    private String name;

    private LocalDate meetDate;

    @Enumerated(EnumType.STRING)
    private PlanetImageType imageType;

    private String inviteCode;

    // Constructor
    public Planet(User user, String name, PlanetImageType imageType) {
        setPlanet(user, this);
        this.name = name;
        this.inviteCode = UUID.randomUUID().toString();
        this.imageType = imageType;
        this.meetDate = LocalDate.now();
    }

    // Convenience Method
    public void join(User user) {
        if (this.owners.size() >= 2 || this.owners.contains(user)) {
            throw new CustomException(ExceptionType.JOIN_PLANET_FAILED);
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
}
