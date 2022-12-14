package trying.cosmos.global.utils.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import trying.cosmos.global.aop.LogSpace;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ImageUtils implements ImageUtils {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    public String create(MultipartFile file) {
        String imageName = UUID.randomUUID() + ImageUtils.getExtension(file);
        upload(imageName, file);
        return imageName;
    }

    private void upload(String imageName, MultipartFile file) {
        try {
            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(file.getInputStream().available());
            amazonS3.putObject(bucket, imageName, file.getInputStream(), meta);
            log.info("{}Upload image name = {}", LogSpace.getSpace(), imageName);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 오류", e);
        }
    }

    public void delete(String name) {
        amazonS3.deleteObject(bucket, name);
        log.info("{}Delete image name = {}", LogSpace.getSpace(), name);
    }
}
