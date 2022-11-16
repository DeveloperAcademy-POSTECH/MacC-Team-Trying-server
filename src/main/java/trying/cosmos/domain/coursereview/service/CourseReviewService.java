package trying.cosmos.domain.coursereview.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.course.repository.CourseRepository;
import trying.cosmos.domain.coursereview.entity.CourseReview;
import trying.cosmos.domain.coursereview.entity.CourseReviewImage;
import trying.cosmos.domain.coursereview.repository.CourseReviewRepository;
import trying.cosmos.domain.notification.entity.NotificationTarget;
import trying.cosmos.domain.notification.service.NotificationService;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.repository.UserRepository;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;
import trying.cosmos.global.utils.image.ImageUtils;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CourseReviewService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final NotificationService notificationService;
    private final MessageSourceAccessor messageSource;
    private final ImageUtils imageUtils;
    private final EntityManager em;

    @Transactional
    public CourseReview create(Long userId, Long courseId, String content, List<MultipartFile> images) {
        User user = userRepository.findById(userId).orElseThrow();
        Course course = courseRepository.searchById(user.getPlanet(), courseId).orElseThrow();
        if (course.isReviewed(user)) {
            throw new CustomException(ExceptionType.DUPLICATED);
        }

        CourseReview review = courseReviewRepository.save(new CourseReview(user, course, content));
        if (images != null) {
            images.forEach(image -> createImage(review, image));
        }

        notificationService.create(
                user.getMate(),
                messageSource.getMessage("notification.review.create.title"),
                messageSource.getMessage("notification.review.create.body", new String[]{course.getTitle()}),
                NotificationTarget.REVIEW,
                course.getId()
        );

        return review;
    }

    public CourseReview find(Long userId, Long reviewId) {
        User user = userRepository.findById(userId).orElseThrow();
        CourseReview review = courseReviewRepository.findById(reviewId).orElseThrow();
        if (!review.getCourse().getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        return review;
    }

    @Transactional
    public void update(Long userId, Long reviewId, String content, List<MultipartFile> images) {
        User user = userRepository.findById(userId).orElseThrow();
        CourseReview review = courseReviewRepository.findById(reviewId).orElseThrow();
        if (!review.getCourse().getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        review.update(content);
        review.getImages().forEach(this::removeExistImages);
        review.getImages().clear();

        if (images != null) {
            images.forEach(image -> createImage(review, image));
        }
    }

    @Transactional
    public void delete(Long userId, Long reviewId) {
        User user = userRepository.findById(userId).orElseThrow();
        CourseReview review = courseReviewRepository.findById(reviewId).orElseThrow();
        if (!review.getCourse().getPlanet().isOwnedBy(user)) {
            throw new CustomException(ExceptionType.NO_DATA);
        }
        review.remove();
        courseReviewRepository.delete(review);
    }

    private void removeExistImages(CourseReviewImage image) {
        imageUtils.delete(image.getName());
        em.remove(image);
    }

    private void createImage(CourseReview review, MultipartFile image) {
        String name = imageUtils.create(image);
        new CourseReviewImage(review, name);
    }
}
