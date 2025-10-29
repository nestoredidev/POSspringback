package com.api.pos_backend.service.implement;

import com.api.pos_backend.service.ClaudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryServiceImplement implements ClaudinaryService {


    private final Cloudinary cloudinary;

    public CloudinaryServiceImplement(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    private File convertFile(MultipartFile multipartFile) throws Exception {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        return file;
//        multipartFile.transferTo(file);
//        return file;
    }

    @Override
    public Map<?, ?> uplolad(MultipartFile multipartFile) throws Exception {
        File file = convertFile(multipartFile);
        Map<String, String> result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        Files.deleteIfExists(file.toPath());
        String url = (String) result.get("secure_url");
        String publicId = (String) result.get("public_id");
        return Map.of(
                "url", url,
                "public_id", publicId
        );
    }

    @Override
    public Map<?, ?> uplolad(File file) throws Exception {
        Map<String, String> result = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
        Files.deleteIfExists(file.toPath());
        String url = (String) result.get("secure_url");
        String publicId = (String) result.get("public_id");
        return Map.of(
                "url", url,
                "public_id", publicId
        );
    }

    @Override
    public Map<?, ?> delete(String id) throws Exception {
        Map<?, ?> res = cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
        return res;
    }
}
