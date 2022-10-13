package trying.cosmos.entity.component;

import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public class CreatedDateEntity {

    @CreatedDate
    private LocalDateTime createdDate;
}
