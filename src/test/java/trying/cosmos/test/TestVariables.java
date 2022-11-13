package trying.cosmos.test;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import trying.cosmos.domain.course.dto.request.CoursePlaceRequest;
import trying.cosmos.domain.place.entity.Place;

import java.util.List;

public abstract class TestVariables {
    // not exist id
    public static final Long NOT_EXIST = 0L;

    // email
    public static final String EMAIL1 = "email1@gmail.com";
    public static final String EMAIL2 = "email2@gmail.com";
    public static final String EMAIL3 = "email3@gmail.com";

    // name
    public static final String NAME1 = "name1";
    public static final String NAME2 = "name2";
    public static final String NAME3 = "name3";

    // password
    public static final String PASSWORD = "password";
    public static final String WRONG_PASSWORD = "wrongpassword";

    // device token
    public static final String DEVICE_TOKEN = "devicetoken";

    // certification code
    public static final String WRONG_CERTIFICATION_CODE = "code";

    // identifier
    public static final String IDENTIFIER1 = "identifier1";
    public static final String IDENTIFIER2 = "identifier2";

    // planet image
    public static final String IMAGE = "image";

    // invite_code
    public static final String INVITE_CODE = "code";
    public static final String WRONG_INVITE_CODE = "wrongcode";

    // course
    public static final String TITLE = "title";
    public static final String BODY = "body";
    public static final String MEMO = "memo";
    public static final Place place1 = new Place(1L, "name", "code", "address", "roadAddress", 0.0, 0.0);
    public static final Place place2 = new Place(2L, "name", "code", "address", "roadAddress", 0.0, 0.0);
    public static final List<CoursePlaceRequest> course_place_request1 = List.of(new CoursePlaceRequest(place1.getId(), MEMO));
    public static final List<CoursePlaceRequest> course_place_request2 = List.of(new CoursePlaceRequest(place2.getId(), MEMO));
    public static final List<CoursePlaceRequest> course_place_not_exist = List.of(new CoursePlaceRequest(0L, "NOT_EXIST"));

    // pageable
    public static final Pageable pageable = PageRequest.of(0, 20);
}
