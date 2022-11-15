package trying.cosmos.docs.utils;

import trying.cosmos.domain.place.entity.Place;

public abstract class DocsVariable {

    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String ACCESS_TOKEN = "accessToken";

    public static final String MY_EMAIL = "me@gmail.com";
    public static final String MATE_EMAIL = "mate@gmail.com";

    public static final String MY_NAME = "ME";
    public static final String MATE_NAME = "MATE";

    public static final String PASSWORD = "!Password1234";
    public static final String DEVICE_TOKEN = "DEVICE_TOKEN";
    public static final String IDENTIFIER = "IDENTIFIER";

    public static final String PLANET_NAME = "PLANET";
    public static final String IMAGE_NAME = "IMAGE";
    public static final String INVITE_CODE = "INVITE_CODE";

    public static final String COURSE_NAME = "COURSE";
    public static final String MEMO = "MEMO";
    public static final String CONTENT = "CONTENT";
    public static final Place PLACE1 = new Place(1L, "Place1", "CODE", "ADDRESS", "ADDRESS", 0.0, 0.0);
    public static final Place PLACE2 = new Place(2L, "Place2", "CODE", "ADDRESS", "ADDRESS", 0.1, 0.2);

    public static final String NOTIFICATION__TITLE = "title";
    public static final String NOTIFICATION__BODY = "body";
    public static final Long NOTIFICATION_TARGET_ID = 1L;
}
