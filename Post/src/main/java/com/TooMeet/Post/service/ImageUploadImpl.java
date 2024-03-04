package com.TooMeet.Post.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageUploadImpl implements ImageUpload {

    private final Cloudinary cloudinary;
    @Override
    public String uploadImage(MultipartFile multipartFile) throws IOException {
        return cloudinary.uploader()
                .upload(multipartFile.getBytes(),
                        Map.of("public_id", UUID.randomUUID().toString()))
                .get("url")
                .toString();
    }

    public void deleteImageById(String imageId) {
        try {
            // Xóa hình ảnh bằng ID
            cloudinary.uploader().destroy(imageId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String extractPublicId(String cloudinaryUrl) {
        int startIndex = cloudinaryUrl.lastIndexOf('/') + 1;
        int endIndex = cloudinaryUrl.lastIndexOf('.');

        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) {
            return null;
        }

        return cloudinaryUrl.substring(startIndex, endIndex);
    }
}
