package trying.cosmos.global.utils.image;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUtils {

    String create(MultipartFile file);

    void delete(String name);

    static String getExtension(MultipartFile file) {
        if (file.getContentType() == null) {
            throw new RuntimeException("이미지 타입이 없습니다.");
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
                throw new RuntimeException("지원하지 않는 이미지 타입입니다.");
        }
    }
}
