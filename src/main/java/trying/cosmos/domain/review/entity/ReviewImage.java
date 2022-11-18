package trying.cosmos.domain.review.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_review_image_id")
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    private String name;

    public ReviewImage(Review review, String name) {
        this.review = review;
        this.name = name;
        review.getImages().add(this);
    }
}
