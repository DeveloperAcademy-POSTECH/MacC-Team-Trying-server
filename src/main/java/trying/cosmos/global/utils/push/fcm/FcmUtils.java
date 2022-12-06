package trying.cosmos.global.utils.push.fcm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import trying.cosmos.domain.user.entity.User;
import trying.cosmos.domain.user.entity.UserStatus;
import trying.cosmos.domain.user.service.UserService;
import trying.cosmos.global.aop.LogSpace;
import trying.cosmos.global.utils.push.PushUtils;
import trying.cosmos.global.utils.push.dto.PushRequest;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmUtils implements PushUtils {

    private static final String GOOGLE_API_URL = "https://www.googleapis.com/auth/cloud-platform";

    @Value("${firebase.appid}")
    private String clientAppId;
    private String clientPushAPI;

    @Value("${firebase.configpath}")
    private String FIREBASE_CONFIG_PATH;

    @PostConstruct
    public void init() {
        this.clientPushAPI = "https://fcm.googleapis.com/v1/projects/" + clientAppId + "/messages:send";
    }

    private final ObjectMapper objectMapper;
    private final UserService userService;

    @Async
    public void pushAll(String title, String body) {
        for (User user : userService.findLoginUsers()) {
            sendMessageTo(user, title, body, null);
        }
    }

    @Async
    public void pushTo(User member, String title, String body, PushRequest.Data data) {
        boolean result = sendMessageTo(member, title, body, data);
        log.info("{}[FCM] Send message {}", LogSpace.getSpace(), result ? "success" : "fail");
    }

    private boolean sendMessageTo(User user, String title, String body, PushRequest.Data data) {
        if (!user.getStatus().equals(UserStatus.LOGIN)) {
            return false;
        }
        if (!user.isAllowNotification()) {
            return false;
        }

        try {
            String message = makeMessage(user.getDeviceToken(), title, body, data);
            log.info("{}[FCM] Send message to {}", LogSpace.getSpace(), user.getId());

            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = RequestBody.create(message, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(clientPushAPI)
                    .post(requestBody)
                    .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                    .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                    .build();

            Response response = client.newCall(request).execute();
            response.close();
            return response.code() == 200;
        } catch (IOException e) {
            throw new RuntimeException("Push Message 전송 오류");
        }
    }

    private String makeMessage(String targetToken, String title, String body, PushRequest.Data data) throws JsonProcessingException {
        PushRequest.Notification notification = new PushRequest.Notification(title, body, null);
        PushRequest.Message message = new PushRequest.Message(targetToken, notification, data);
        PushRequest pushRequest = new PushRequest(false, message);
        return objectMapper.writeValueAsString(pushRequest);
    }

    private String getAccessToken() {
        try {
            GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
                    new ClassPathResource(FIREBASE_CONFIG_PATH).getInputStream()).createScoped(List.of(GOOGLE_API_URL));
            googleCredentials.refreshIfExpired();
            return googleCredentials.getAccessToken().getTokenValue();
        } catch (IOException e) {
            throw new RuntimeException("Google 인증 오류");
        }
    }
}