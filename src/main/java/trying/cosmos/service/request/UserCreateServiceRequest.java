package trying.cosmos.service.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import trying.cosmos.controller.request.UserCreateControllerRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserCreateServiceRequest extends UserCreateControllerRequest {

    private String email;
    private String name;
    public UserCreateServiceRequest(String email, UserCreateControllerRequest request) {
        this.email = email;
        this.name = request.getName();
    }
}
