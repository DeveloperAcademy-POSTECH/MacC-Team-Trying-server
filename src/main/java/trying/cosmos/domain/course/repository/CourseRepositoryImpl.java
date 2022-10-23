package trying.cosmos.domain.course.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import trying.cosmos.domain.course.entity.Access;
import trying.cosmos.domain.course.entity.Course;
import trying.cosmos.domain.user.entity.User;

import java.util.List;

import static trying.cosmos.domain.course.entity.QCourse.course;
import static trying.cosmos.domain.planet.entity.QPlanet.planet;
import static trying.cosmos.domain.planet.entity.QPlanetFollow.planetFollow;

@RequiredArgsConstructor
public class CourseRepositoryImpl implements CourseRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Course> getFeed(User user, Pageable pageable) {
        List<Course> contents = queryFactory.select(course)
                .from(course)
                .where(
                        feedCondition(user)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(course.createdDate.desc())
                .fetch();

        boolean hasNext = (contents.size() > pageable.getPageSize());
        if (hasNext) {
            contents.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    private BooleanBuilder feedCondition(User user) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(isMyCourse(user));
        builder.or(isFollowPublicCourse(user));
        builder.and(isPlanetNotDeleted());
        builder.and(isCourseNotDeleted());
        return builder;
    }

    // 내 행성의 모든 코스 조건
    private BooleanExpression isMyCourse(User user) {
        return course.planet.eq(user.getPlanet());
    }

    // 내가 팔로우 하고 있는 행성의 공개 코스
    private BooleanExpression isFollowPublicCourse(User user) {
        return course.planet.in(
                JPAExpressions
                        .select(planetFollow.planet)
                        .from(planetFollow)
                        .where(planetFollow.user.eq(user))
        ).and(course.access.eq(Access.PUBLIC));
    }

    // 행성이 삭제 X
    private BooleanExpression isPlanetNotDeleted() {
        return planet.isDeleted.isFalse();
    }

    // 코스가 삭제 X
    private BooleanExpression isCourseNotDeleted() {
        return course.isDeleted.isFalse();
    }
}
