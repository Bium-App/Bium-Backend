package memo.example.demo.controller;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import memo.example.demo.DTO.response.PresignedUrlResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

/**
 * 클라이언트가 S3에 파일을 직접 업로드할 수 있는 임시 URL을 발급한다.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FileController {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @GetMapping("/files/presigned-url")
    public ResponseEntity<PresignedUrlResponseDto> getPresignedUrl(
            @RequestParam(name = "fileName") String fileName,
            @RequestParam(name = "fileType") String fileType,
            @RequestParam(name = "domain") String domain) {

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        String objectKey = domain + "/" + UUID.randomUUID() + "_" + fileName;
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, objectKey)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);
        generatePresignedUrlRequest.setContentType(fileType);

        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return ResponseEntity.ok(PresignedUrlResponseDto.builder()
                .presignedUrl(url.toString())
                .fileUrl("https://" + bucket + ".s3." + region + ".amazonaws.com/" + objectKey)
                .build());
    }
}
