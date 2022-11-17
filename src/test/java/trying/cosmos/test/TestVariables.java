package trying.cosmos.test;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import trying.cosmos.domain.course.dto.request.CoursePlaceRequest;
import trying.cosmos.domain.place.dto.request.PlaceCreateRequest;

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
    public static final Long PLACE_IDENTIFIER1 = 1L;
    public static final Long PLACE_IDENTIFIER2 = 2L;
    public static final String CATEGORY1 = "CATEGORY1";
    public static final String CATEGORY2 = "CATEGORY1";
    public static final List<CoursePlaceRequest> course_place_request1 = List.of(new CoursePlaceRequest(new PlaceCreateRequest(1L, "name", "code",  0.0, 0.0), MEMO));
    public static final List<CoursePlaceRequest> course_place_request2 = List.of(new CoursePlaceRequest(new PlaceCreateRequest(2L, "name", "code",  0.3, 0.5), MEMO));

    // pageable
    public static final Pageable pageable = PageRequest.of(0, 20);
}
