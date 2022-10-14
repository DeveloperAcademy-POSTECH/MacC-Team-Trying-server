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

    private String inviteCode;

    @Enumerated(EnumType.STRING)
    private PlanetImageType imageType;

    // Constructor
    public Planet(User user, String name, PlanetImageType imageType) {
        setPlanet(user, this);
        this.name = name;
        this.imageType = imageType;
        this.inviteCode = UUID.randomUUID().toString();
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

    public String getInviteCode(User user) {
        if (!this.owners.contains(user)) {
            throw new CustomException(ExceptionType.NO_PERMISSION);
        }
        return this.inviteCode;
    }

    public List<String> getOwnersName() {
        return this.owners.stream().map(User::getName).collect(Collectors.toList());
    }

    public void updateDday(int days) {
        this.meetDate = LocalDate.now().minusDays(days);
    }

    public int getDday() {
        LocalDate from = this.meetDate == null ? getCreatedDate().toLocalDate() : this.meetDate;
        return (int) Duration.between(from.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays() + 1;
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
}
