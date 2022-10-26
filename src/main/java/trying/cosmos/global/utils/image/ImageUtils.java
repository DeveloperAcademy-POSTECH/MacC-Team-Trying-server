package trying.cosmos.global.utils.image;

import org.springframework.web.multipart.MultipartFile;
import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

public interface ImageUtils {

    String create(MultipartFile file);

    void delete(String name);

    static String getExtension(MultipartFile file) {
        if (file.getContentType() == null) {
            throw new CustomException(ExceptionType.INVALID_IMAGE_TYPE);
        }

        switch (file.getContentType()) {
            case "image/png":
                return ".png";
            case "image/jpeg":
                return ".jpeg";
            case "image/heic":
            case "image/heif":
                return ".heic";
            default:
                throw new CustomException(ExceptionType.INVALID_IMAGE_TYPE);
        }
    }
}
